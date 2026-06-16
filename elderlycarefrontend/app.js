const BASE_URL = 'http://localhost:8080';
let currentUser = null;
let token = localStorage.getItem('token');
let lastSavedRadarData = null;

function showPage(pageId) {
    document.querySelectorAll('.page').forEach(page => page.classList.remove('active'));
    document.querySelectorAll('.nav-link').forEach(link => link.classList.remove('active'));

    if (pageId === 'login-page') {
        document.getElementById(pageId).classList.add('active');
        document.getElementById('navbar').classList.add('hidden');
    } else {
        document.getElementById(pageId).classList.add('active');
        const navLink = document.querySelector(`[onclick="showPage('${pageId}')"]`);
        if (navLink) navLink.classList.add('active');
        document.getElementById('navbar').classList.remove('hidden');
    }

    if (pageId === 'dashboard') loadDashboard();
    if (pageId === 'devices') loadDevices();
    if (pageId === 'users') loadUsers();
    if (pageId === 'alerts') loadAlerts();
    if (pageId === 'ai-test') loadAiTestDevices();
}

function switchTab(tab) {
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.form-container').forEach(form => form.classList.add('hidden'));

    document.querySelector(`.tab-btn[onclick="switchTab('${tab}')"]`).classList.add('active');
    document.getElementById(`${tab}-form`).classList.remove('hidden');
}

async function login() {
    const username = document.getElementById('login-username').value.trim();
    const password = document.getElementById('login-password').value;

    if (!username || !password) {
        alert('请填写用户名和密码');
        return;
    }

    try {
        const response = await fetch(`${BASE_URL}/api/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        const result = await response.json();

        if (result.code === 200) {
            token = result.data;
            localStorage.setItem('token', token);

            currentUser = await getUserInfo();
            if (!currentUser) {
                const userId = getUserIdFromToken(token);
                currentUser = { id: userId, name: username, username: username };
            }

            showPage('dashboard');
        } else {
            alert(result.message || '登录失败');
        }
    } catch (error) {
        alert('登录失败，请检查后端服务是否启动: ' + error.message);
    }
}

function getUserIdFromToken(token) {
    try {
        const payload = token.split('.')[1];
        const decoded = atob(payload);
        const data = JSON.parse(decoded);
        return parseInt(data.sub) || 1;
    } catch {
        return 1;
    }
}

async function register() {
    const username = document.getElementById('reg-username').value.trim();
    const password = document.getElementById('reg-password').value;
    const name = document.getElementById('reg-name').value.trim();
    const phone = document.getElementById('reg-phone').value.trim();
    const familyPhone = document.getElementById('reg-family-phone').value.trim();

    if (!username || !password || !name || !phone || !familyPhone) {
        alert('请填写所有必填项（用户名、密码、姓名、手机号、家属电话）');
        return;
    }

    const user = {
        username: username,
        password: password,
        name: name,
        phone: phone,
        familyPhone: familyPhone,
        address: document.getElementById('reg-address').value.trim(),
        age: parseInt(document.getElementById('reg-age').value) || null,
        gender: document.getElementById('reg-gender').value || null,
        userType: document.getElementById('reg-user-type').value || 'ELDERLY'
    };

    try {
        const response = await fetch(`${BASE_URL}/api/user/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(user)
        });

        const result = await response.json();

        if (result.code === 200) {
            alert('注册成功，请登录');
            switchTab('login');
        } else {
            alert(result.message || '注册失败');
        }
    } catch (error) {
        alert('注册失败，请检查后端服务是否启动: ' + error.message);
    }
}

async function getUserInfo() {
    if (!token) return null;

    try {
        const response = await fetch(`${BASE_URL}/api/user/info`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        const result = await response.json();

        if (result.code === 200) {
            return result.data;
        }
    } catch (error) {
        console.error('获取用户信息失败:', error);
    }
    return null;
}

async function getTestToken() {
    try {
        const response = await fetch(`${BASE_URL}/api/auth/wechat/test-token`);
        const result = await response.json();

        if (result.code === 200) {
            token = result.data;
            localStorage.setItem('token', token);

            currentUser = await getUserInfo();
            if (!currentUser) {
                const userId = getUserIdFromToken(token);
                currentUser = { id: userId, name: '测试用户', username: 'test' };
            }

            showPage('dashboard');
        } else {
            alert('获取测试Token失败');
        }
    } catch (error) {
        alert('获取测试Token失败，请检查后端服务是否启动: ' + error.message);
    }
}

function logout() {
    token = null;
    localStorage.removeItem('token');
    currentUser = null;
    showPage('login-page');
}

async function loadDashboard() {
    if (!currentUser) {
        currentUser = await getUserInfo();
        if (!currentUser) {
            currentUser = { id: 1, name: '用户', username: 'user' };
        }
    }

    if (currentUser) {
        document.getElementById('current-user').textContent = currentUser.name || currentUser.username;
    }

    await Promise.all([
        loadDeviceCount(),
        loadAlertCount(),
        loadUserCount(),
        loadRecentAlerts(),
        loadDeviceStatusList()
    ]);
}

async function loadDeviceCount() {
    try {
        const response = await fetch(`${BASE_URL}/api/edge/devices/user/${currentUser?.id || 1}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        const result = await response.json();
        if (result.code === 200) {
            document.getElementById('device-count').textContent = result.data.length;
        }
    } catch (error) {
        console.error('获取设备数量失败:', error);
    }
}

async function loadAlertCount() {
    try {
        const response = await fetch(`${BASE_URL}/api/alert/history?page=0&size=100`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        const result = await response.json();
        if (result.code === 200) {
            const alerts = result.data.content || [];
            const pendingCount = alerts.filter(a => a.status === 'PENDING').length;
            document.getElementById('alert-count').textContent = pendingCount;

            const badge = document.getElementById('alert-badge');
            if (pendingCount > 0) {
                badge.textContent = pendingCount;
                badge.classList.remove('hidden');
            } else {
                badge.classList.add('hidden');
            }
        }
    } catch (error) {
        console.error('获取告警数量失败:', error);
    }
}

async function loadUserCount() {
    try {
        const response = await fetch(`${BASE_URL}/api/user/list`);

        const result = await response.json();
        if (result.code === 200) {
            document.getElementById('user-count').textContent = result.data.length;
        }
    } catch (error) {
        console.error('获取用户数量失败:', error);
    }
}

async function loadRecentAlerts() {
    try {
        const response = await fetch(`${BASE_URL}/api/alert/history?page=0&size=5`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        const result = await response.json();

        if (result.code === 200 && result.data.content) {
            const container = document.getElementById('recent-alerts-list');
            container.innerHTML = '';

            result.data.content.forEach(alert => {
                const item = document.createElement('div');
                item.className = 'alert-item';
                item.innerHTML = `
                    <div>
                        <span class="alert-type">${getAlertTypeText(alert.alertType)}</span>
                        ${alert.deviceId ? `<br><small>设备: ${alert.deviceId}</small>` : ''}
                    </div>
                    <div class="alert-time">${formatDate(alert.createTime)}</div>
                `;
                container.appendChild(item);
            });

            if (result.data.content.length === 0) {
                container.innerHTML = '<p style="padding: 1rem; text-align: center; color: #999;">暂无告警记录</p>';
            }
        }
    } catch (error) {
        console.error('获取最近告警失败:', error);
    }
}

async function loadDeviceStatusList() {
    try {
        const response = await fetch(`${BASE_URL}/api/edge/devices/user/${currentUser?.id || 1}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        const result = await response.json();

        if (result.code === 200) {
            const container = document.getElementById('device-status-list');
            container.innerHTML = '';

            result.data.forEach(device => {
                const item = document.createElement('div');
                item.className = 'device-status-item';
                const isOnline = device.status && device.status.toUpperCase() === 'ONLINE';
                item.innerHTML = `
                    <div>
                        <div style="font-weight: bold;">${device.deviceId}</div>
                        <div class="device-location">${device.location || '未知位置'}</div>
                    </div>
                    <span class="device-status-badge ${isOnline ? 'device-status-online' : 'device-status-offline'}">
                        ${isOnline ? '在线' : '离线'}
                    </span>
                `;
                container.appendChild(item);
            });

            if (result.data.length === 0) {
                container.innerHTML = '<p style="padding: 1rem; text-align: center; color: #999;">暂无设备</p>';
            }
        }
    } catch (error) {
        console.error('获取设备状态失败:', error);
    }
}

function formatDate(dateStr) {
    if (!dateStr) return '-';
    const date = new Date(dateStr);
    return date.toLocaleString('zh-CN', {
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function getAlertTypeText(type) {
    if (!type) return '未知告警';
    switch (type.toUpperCase()) {
        case 'FALL': return '⚠️ 跌倒告警';
        case 'SITTING': return '⏰ 静止告警';
        default: return type;
    }
}

async function loadDevices() {
    try {
        const response = await fetch(`${BASE_URL}/api/edge/devices/user/${currentUser?.id || 1}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        const result = await response.json();

        if (result.code === 200) {
            renderDeviceTable(result.data);
        }
    } catch (error) {
        console.error('获取设备列表失败:', error);
    }
}

function renderDeviceTable(devices) {
    const container = document.getElementById('device-list');

    if (!devices || devices.length === 0) {
        container.innerHTML = '<p style="padding: 2rem; text-align: center; color: #999;">暂无设备</p>';
        return;
    }

    window.deviceList = devices;

    container.innerHTML = `
        <table>
            <thead>
                <tr>
                    <th>设备ID</th>
                    <th>位置</th>
                    <th>状态</th>
                    <th>关联用户</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
                ${devices.map((device, index) => `
                    <tr>
                        <td>${device.deviceId}</td>
                        <td>${device.location || '-'}</td>
                        <td><span class="status-badge ${getDeviceStatusClass(device.status)}">${getStatusText(device.status)}</span></td>
                        <td>${device.user?.name || '-'}</td>
                        <td>
                            <div class="action-btns">
                                <button class="action-btn edit" data-index="${index}">编辑</button>
                                <button class="action-btn delete" data-deviceid="${device.deviceId}">删除</button>
                            </div>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;

    container.querySelectorAll('.action-btn.edit').forEach(btn => {
        btn.addEventListener('click', function() {
            const index = parseInt(this.getAttribute('data-index'));
            editDevice(index);
        });
    });

    container.querySelectorAll('.action-btn.delete').forEach(btn => {
        btn.addEventListener('click', function() {
            const deviceId = this.getAttribute('data-deviceid');
            deleteDevice(deviceId);
        });
    });
}

function getDeviceStatusClass(status) {
    if (!status) return 'status-pending';
    switch (status.toUpperCase()) {
        case 'ONLINE': return 'status-resolved';
        case 'OFFLINE': return 'status-pending';
        default: return 'status-confirmed';
    }
}

function getStatusText(status) {
    if (!status) return '未知';
    switch (status.toUpperCase()) {
        case 'ONLINE': return '在线';
        case 'OFFLINE': return '离线';
        default: return status;
    }
}

function showDeviceModal(device = null) {
    const modal = document.getElementById('device-modal');
    document.getElementById('device-modal-title').textContent = device ? '编辑设备' : '添加设备';

    if (device) {
        document.getElementById('device-db-id').value = device.id || '';
        document.getElementById('original-device-id').value = device.deviceId;
        document.getElementById('modal-device-id').value = device.deviceId;
        document.getElementById('modal-location').value = device.location || '';
        document.getElementById('modal-user-id').value = device.user?.id || '';
    } else {
        document.getElementById('device-db-id').value = '';
        document.getElementById('original-device-id').value = '';
        document.getElementById('modal-device-id').value = '';
        document.getElementById('modal-location').value = '';
        document.getElementById('modal-user-id').value = currentUser?.id || '';
    }

    loadUsersForSelect();
    modal.style.display = 'block';
}

function editDevice(index) {
    const device = window.deviceList[index];
    showDeviceModal(device);
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

async function loadUsersForSelect() {
    try {
        const response = await fetch(`${BASE_URL}/api/user/list`);

        const result = await response.json();

        if (result.code === 200) {
            const select = document.getElementById('modal-user-id');
            select.innerHTML = result.data.map(user =>
                `<option value="${user.id}">${user.name} (${user.username})</option>`
            ).join('');
        }
    } catch (error) {
        console.error('获取用户列表失败:', error);
    }
}

async function saveDevice() {
    const deviceId = document.getElementById('modal-device-id').value.trim();
    const location = document.getElementById('modal-location').value.trim();
    const userId = parseInt(document.getElementById('modal-user-id').value);
    const deviceDbId = document.getElementById('device-db-id').value;
    const originalDeviceId = document.getElementById('original-device-id').value;

    if (!deviceId || !location || !userId) {
        alert('请填写所有必填项');
        return;
    }

    const device = {
        deviceId: deviceId,
        location: location,
        user: { id: userId }
    };

    try {
        let url = `${BASE_URL}/api/edge/devices`;
        let method = 'POST';

        if (deviceDbId && originalDeviceId) {
            url = `${BASE_URL}/api/edge/devices/${originalDeviceId}`;
            method = 'PUT';
        }

        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(device)
        });

        const result = await response.json();

        if (result.code === 200) {
            alert(deviceDbId ? '设备更新成功' : '设备添加成功');
            closeModal('device-modal');
            loadDevices();
        } else {
            alert(result.message || '保存失败');
        }
    } catch (error) {
        alert('保存失败: ' + error.message);
    }
}

async function deleteDevice(deviceId) {
    if (!confirm('确定要删除这个设备吗？')) return;

    try {
        const response = await fetch(`${BASE_URL}/api/edge/devices/${deviceId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        const result = await response.json();

        if (result.code === 200) {
            alert('删除成功');
            loadDevices();
        } else {
            alert(result.message || '删除失败');
        }
    } catch (error) {
        alert('删除失败: ' + error.message);
    }
}

async function loadUsers() {
    try {
        const response = await fetch(`${BASE_URL}/api/user/list`);

        const result = await response.json();

        if (result.code === 200) {
            renderUserTable(result.data);
        }
    } catch (error) {
        console.error('获取用户列表失败:', error);
    }
}

function renderUserTable(users) {
    const container = document.getElementById('user-list');

    if (!users || users.length === 0) {
        container.innerHTML = '<p style="padding: 2rem; text-align: center; color: #999;">暂无用户</p>';
        return;
    }

    window.userList = users;

    container.innerHTML = `
        <table>
            <thead>
                <tr>
                    <th>用户名</th>
                    <th>姓名</th>
                    <th>性别</th>
                    <th>年龄</th>
                    <th>手机号</th>
                    <th>用户类型</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
                ${users.map((user, index) => `
                    <tr>
                        <td>${user.username || '-'}</td>
                        <td>${user.name || '-'}</td>
                        <td>${user.gender || '-'}</td>
                        <td>${user.age || '-'}</td>
                        <td>${user.phone || '-'}</td>
                        <td>${getUserTypeText(user.userType)}</td>
                        <td>
                            <div class="action-btns">
                                <button class="action-btn edit" data-index="${index}">编辑</button>
                                <button class="action-btn delete" data-userid="${user.id}">删除</button>
                            </div>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;

    container.querySelectorAll('.action-btn.edit').forEach(btn => {
        btn.addEventListener('click', function() {
            const index = parseInt(this.getAttribute('data-index'));
            editUser(index);
        });
    });

    container.querySelectorAll('.action-btn.delete').forEach(btn => {
        btn.addEventListener('click', function() {
            const userId = parseInt(this.getAttribute('data-userid'));
            deleteUser(userId);
        });
    });
}

function getUserTypeText(type) {
    if (!type) return '-';
    switch (type.toUpperCase()) {
        case 'ELDERLY': return '👴 老人';
        case 'FAMILY': return '👨‍👩‍👧 家属';
        case 'ADMIN': return '🔧 管理员';
        default: return type;
    }
}

function showUserModal(user = null) {
    const modal = document.getElementById('user-modal');
    document.getElementById('user-modal-title').textContent = user ? '编辑用户' : '添加用户';

    if (user) {
        document.getElementById('user-db-id').value = user.id;
        document.getElementById('modal-username').value = user.username || '';
        document.getElementById('modal-password').value = '';
        document.getElementById('modal-name').value = user.name || '';
        document.getElementById('modal-phone').value = user.phone || '';
        document.getElementById('modal-family-phone').value = user.familyPhone || '';
        document.getElementById('modal-address').value = user.address || '';
        document.getElementById('modal-age').value = user.age || '';
        document.getElementById('modal-gender').value = user.gender || '男';
        document.getElementById('modal-user-type').value = user.userType || 'ELDERLY';
    } else {
        document.getElementById('user-db-id').value = '';
        document.getElementById('modal-username').value = '';
        document.getElementById('modal-password').value = '';
        document.getElementById('modal-name').value = '';
        document.getElementById('modal-phone').value = '';
        document.getElementById('modal-family-phone').value = '';
        document.getElementById('modal-address').value = '';
        document.getElementById('modal-age').value = '';
        document.getElementById('modal-gender').value = '男';
        document.getElementById('modal-user-type').value = 'ELDERLY';
    }

    modal.style.display = 'block';
}

function editUser(index) {
    const user = window.userList[index];
    showUserModal(user);
}

async function saveUser() {
    const userDbId = document.getElementById('user-db-id').value;
    const username = document.getElementById('modal-username').value.trim();
    const password = document.getElementById('modal-password').value;
    const name = document.getElementById('modal-name').value.trim();
    const phone = document.getElementById('modal-phone').value.trim();
    const familyPhone = document.getElementById('modal-family-phone').value.trim();

    if (!username || !name || !phone || !familyPhone) {
        alert('请填写所有必填项');
        return;
    }

    const user = {
        username: username,
        name: name,
        phone: phone,
        familyPhone: familyPhone,
        address: document.getElementById('modal-address').value.trim(),
        age: parseInt(document.getElementById('modal-age').value) || null,
        gender: document.getElementById('modal-gender').value,
        userType: document.getElementById('modal-user-type').value
    };

    if (password) {
        user.password = password;
    }

    try {
        const url = userDbId ? `${BASE_URL}/api/user/${userDbId}` : `${BASE_URL}/api/user/register`;
        const response = await fetch(url, {
            method: userDbId ? 'PUT' : 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(user)
        });

        const result = await response.json();

        if (result.code === 200) {
            alert(userDbId ? '用户更新成功' : '用户添加成功');
            closeModal('user-modal');
            loadUsers();
        } else {
            alert(result.message || '保存失败');
        }
    } catch (error) {
        alert('保存失败: ' + error.message);
    }
}

async function deleteUser(userId) {
    if (!confirm('确定要删除这个用户吗？')) return;

    try {
        const response = await fetch(`${BASE_URL}/api/user/${userId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        const result = await response.json();

        if (result.code === 200) {
            alert('删除成功');
            loadUsers();
        } else {
            alert(result.message || '删除失败');
        }
    } catch (error) {
        alert('删除失败: ' + error.message);
    }
}

async function loadAlerts() {
    const status = document.getElementById('alert-status-filter').value;
    const type = document.getElementById('alert-type-filter').value;

    try {
        const response = await fetch(`${BASE_URL}/api/alert/history?page=0&size=50`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        const result = await response.json();

        if (result.code === 200) {
            let alerts = result.data.content || [];

            if (status !== 'ALL') {
                alerts = alerts.filter(alert => alert.status === status);
            }

            if (type !== 'ALL') {
                alerts = alerts.filter(alert => alert.alertType === type);
            }

            renderAlertTable(alerts);
        }
    } catch (error) {
        console.error('获取告警列表失败:', error);
    }
}

function renderAlertTable(alerts) {
    const container = document.getElementById('alert-table');

    if (!alerts || alerts.length === 0) {
        container.innerHTML = '<p style="padding: 2rem; text-align: center; color: #999;">暂无告警记录</p>';
        return;
    }

    window.alertList = alerts;

    container.innerHTML = `
        <table>
            <thead>
                <tr>
                    <th>告警类型</th>
                    <th>设备ID</th>
                    <th>状态</th>
                    <th>AI分析</th>
                    <th>时间</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
                ${alerts.map((alert, index) => `
                    <tr>
                        <td>${getAlertTypeText(alert.alertType)}</td>
                        <td>${alert.deviceId || '-'}</td>
                        <td><span class="status-badge ${getAlertStatusClass(alert.status)}">${getAlertStatusText(alert.status)}</span></td>
                        <td>${alert.aiAnalysisResult ? '<span style="color: #4CAF50;">已分析</span>' : '<span style="color: #999;">未分析</span>'}</td>
                        <td>${formatDate(alert.createTime)}</td>
                        <td>
                            <div class="action-btns">
                                <button class="action-btn view" data-index="${index}">详情</button>
                                ${alert.status === 'PENDING' ? `<button class="action-btn resolve" data-index="${index}">处理</button>` : ''}
                            </div>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;

    container.querySelectorAll('.action-btn.view').forEach(btn => {
        btn.addEventListener('click', function() {
            const index = parseInt(this.getAttribute('data-index'));
            viewAlert(index);
        });
    });

    container.querySelectorAll('.action-btn.resolve').forEach(btn => {
        btn.addEventListener('click', function() {
            const index = parseInt(this.getAttribute('data-index'));
            resolveAlert(index);
        });
    });
}

function getAlertStatusClass(status) {
    if (!status) return 'status-pending';
    switch (status.toUpperCase()) {
        case 'PENDING': return 'status-pending';
        case 'CONFIRMED': return 'status-confirmed';
        case 'RESOLVED': return 'status-resolved';
        default: return 'status-pending';
    }
}

function getAlertStatusText(status) {
    if (!status) return '未知';
    switch (status.toUpperCase()) {
        case 'PENDING': return '待处理';
        case 'CONFIRMED': return '已确认';
        case 'RESOLVED': return '已解决';
        default: return status;
    }
}

function viewAlert(index) {
    const alert = window.alertList[index];
    const modal = document.getElementById('alert-detail-modal');
    const statusBadge = document.getElementById('alert-detail-status');
    const details = document.getElementById('alert-detail-content');
    const actions = document.getElementById('alert-detail-actions');

    statusBadge.className = `status-badge ${getAlertStatusClass(alert.status)}`;
    statusBadge.textContent = getAlertStatusText(alert.status);

    let aiAnalysisHtml = '<div><strong>AI分析:</strong> 暂无分析结果</div>';
    if (alert.aiAnalysisResult) {
        let aiResult = alert.aiAnalysisResult;
        try {
            if (typeof aiResult === 'string' && aiResult.includes('{')) {
                const jsonMatch = aiResult.match(/\{[^}]+\}/);
                if (jsonMatch) {
                    const parsed = JSON.parse(jsonMatch[0]);
                    aiResult = parsed;
                }
            }

            if (typeof aiResult === 'object' && aiResult.riskLevel) {
                aiAnalysisHtml = `
                    <div style="margin-top: 15px; padding: 12px; background: #f8f9fa; border-radius: 8px; border-left: 4px solid #667eea;">
                        <div style="font-weight: bold; color: #667eea; margin-bottom: 8px;">🤖 AI分析结果</div>
                        <div style="margin-bottom: 8px;">
                            <strong>风险等级:</strong>
                            <span style="color: ${getRiskColor(aiResult.riskLevel)}; font-weight: bold;">${aiResult.riskLevel}</span>
                        </div>
                        <div style="margin-bottom: 8px;"><strong>分析结论:</strong> ${aiResult.conclusion || '-'}</div>
                        <div style="margin-bottom: 8px;"><strong>判断理由:</strong> ${aiResult.reason || '-'}</div>
                        <div><strong>处理建议:</strong> ${aiResult.suggestion || '-'}</div>
                    </div>
                `;
            } else {
                aiAnalysisHtml = `
                    <div style="margin-top: 15px; padding: 12px; background: #f8f9fa; border-radius: 8px; border-left: 4px solid #667eea;">
                        <div style="font-weight: bold; color: #667eea; margin-bottom: 8px;">🤖 AI分析结果</div>
                        <div style="word-break: break-all;">${alert.aiAnalysisResult}</div>
                    </div>
                `;
            }
        } catch (e) {
            aiAnalysisHtml = `
                <div style="margin-top: 15px; padding: 12px; background: #f8f9fa; border-radius: 8px;">
                    <div style="font-weight: bold; color: #667eea; margin-bottom: 5px;">🤖 AI分析结果</div>
                    <div>${alert.aiAnalysisResult}</div>
                </div>
            `;
        }
    }

    details.innerHTML = `
        <div><strong>告警类型:</strong> ${getAlertTypeText(alert.alertType)}</div>
        <div><strong>设备ID:</strong> ${alert.deviceId || '-'}</div>
        <div><strong>用户ID:</strong> ${alert.userId || '-'}</div>
        <div><strong>状态:</strong> ${getAlertStatusText(alert.status)}</div>
        <div><strong>发生时间:</strong> ${formatDate(alert.createTime)}</div>
        ${aiAnalysisHtml}
    `;

    let actionButtons = '';
    if (alert.status === 'PENDING') {
        actionButtons = `
            <button class="btn btn-success" onclick="resolveAlert(${index})">标记已处理</button>
        `;
    }

    actions.innerHTML = actionButtons;
    modal.style.display = 'block';
}

function getRiskColor(riskLevel) {
    if (!riskLevel) return '#666';
    switch (riskLevel.toUpperCase()) {
        case 'HIGH': return '#c62828';
        case 'MEDIUM': return '#ff9800';
        case 'LOW': return '#2e7d32';
        default: return '#666';
    }
}

async function resolveAlert(index) {
    const alert = window.alertList[index];

    try {
        const response = await fetch(`${BASE_URL}/api/alert/resolve/${alert.id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        const result = await response.json();

        if (result.code === 200) {
            alert('告警已处理');
            closeModal('alert-detail-modal');
            loadAlerts();
        } else {
            alert(result.message || '处理失败');
        }
    } catch (error) {
        alert('处理失败: ' + error.message);
    }
}

async function loadAiTestDevices() {
    try {
        const response = await fetch(`${BASE_URL}/api/edge/devices/user/${currentUser?.id || 1}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        const result = await response.json();

        if (result.code === 200) {
            const select = document.getElementById('ai-device');
            select.innerHTML = '<option value="">请选择设备</option>' + result.data.map(device =>
                `<option value="${device.deviceId}">${device.deviceId} - ${device.location || '未知位置'}</option>`
            ).join('');
        }
    } catch (error) {
        console.error('获取设备列表失败:', error);
    }

    lastSavedRadarData = null;
    clearAiResult();
}

function clearAiResult() {
    const resultContent = document.getElementById('ai-result-content');
    const resultStatus = document.getElementById('result-status');

    resultStatus.className = 'status-badge status-pending';
    resultStatus.textContent = '等待分析';

    resultContent.innerHTML = `
        <div class="empty-state">
            <div class="empty-icon">🤖</div>
            <p>请先上传雷达数据，然后进行AI分析</p>
            <p class="empty-hint">步骤：1.上传雷达数据 → 2.执行AI分析</p>
        </div>
    `;
}

document.getElementById('ai-speed').addEventListener('input', function() {
    document.getElementById('ai-speed-value').textContent = this.value;
});

document.getElementById('ai-trajectoryx').addEventListener('input', function() {
    document.getElementById('ai-trajectoryx-value').textContent = this.value;
});

document.getElementById('ai-trajectoryy').addEventListener('input', function() {
    document.getElementById('ai-trajectoryy-value').textContent = this.value;
});

function showProgress(step) {
    const progressContainer = document.getElementById('ai-progress');
    const progressFill = document.getElementById('ai-progress-fill');
    const resultContent = document.getElementById('ai-result-content');
    const resultStatus = document.getElementById('result-status');

    progressContainer.classList.remove('hidden');
    resultContent.innerHTML = '';
    resultStatus.className = 'status-badge status-pending';
    resultStatus.textContent = '处理中';

    progressFill.style.width = '0%';

    const steps = document.querySelectorAll('.progress-steps .step');
    steps.forEach((stepEl, index) => {
        stepEl.classList.remove('active');
        setTimeout(() => {
            stepEl.classList.add('active');
            progressFill.style.width = `${(index + 1) * 33}%`;
        }, index * 500);
    });
}

function hideProgress() {
    document.getElementById('ai-progress').classList.add('hidden');
}

async function sendRadarData() {
    const deviceId = document.getElementById('ai-device').value;
    if (!deviceId) {
        alert('请先选择设备');
        return;
    }

    showProgress();

    const speed = parseFloat(document.getElementById('ai-speed').value);
    const trajectoryx = parseFloat(document.getElementById('ai-trajectoryx').value);
    const trajectoryy = parseFloat(document.getElementById('ai-trajectoryy').value);
    const rawData = document.getElementById('ai-rawdata').value || '{"sensor":"radar","data":[1,2,3,4,5]}';

    const radarData = {
        edgeDevice: { deviceId: deviceId },
        speed: speed,
        trajectoryX: trajectoryx,
        trajectoryY: trajectoryy,
        rawData: rawData
    };

    try {
        const response = await fetch(`${BASE_URL}/api/radar/data`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(radarData)
        });

        const result = await response.json();

        hideProgress();

        if (result.code === 200) {
            lastSavedRadarData = result.data;
            displayRadarResult('雷达数据上传成功', deviceId, speed, trajectoryx, trajectoryy, result.data.id);
        } else {
            displayErrorResult('上传失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        hideProgress();
        displayErrorResult('上传失败: ' + error.message);
    }
}

function displayRadarResult(message, deviceId, speed, trajectoryx, trajectoryy, dataId) {
    const resultContent = document.getElementById('ai-result-content');
    const resultStatus = document.getElementById('result-status');

    resultStatus.className = 'status-badge status-resolved';
    resultStatus.textContent = '已上传';

    resultContent.innerHTML = `
        <div class="ai-result-card risk-low">
            <div class="ai-result-header">
                <span class="ai-result-icon">📡</span>
                <span class="ai-result-title">雷达数据上传结果</span>
            </div>
            <div class="ai-result-grid">
                <div class="ai-result-item">
                    <div class="ai-result-item-label">📊 设备ID</div>
                    <div class="ai-result-item-value">${deviceId}</div>
                </div>
                <div class="ai-result-item">
                    <div class="ai-result-item-label">🔢 数据ID</div>
                    <div class="ai-result-item-value">${dataId || '-'}</div>
                </div>
                <div class="ai-result-item">
                    <div class="ai-result-item-label">⚡ 速度</div>
                    <div class="ai-result-item-value">${speed} m/s</div>
                </div>
                <div class="ai-result-item">
                    <div class="ai-result-item-label">📈 轨迹X</div>
                    <div class="ai-result-item-value">${trajectoryx}</div>
                </div>
                <div class="ai-result-item">
                    <div class="ai-result-item-label">📉 轨迹Y</div>
                    <div class="ai-result-item-value">${trajectoryy}</div>
                </div>
            </div>
            <div class="ai-result-risk low" style="margin-top: 1rem;">
                <div class="ai-result-risk-title">✅ ${message}</div>
                <div style="font-size: 0.85rem; color: #666; margin-top: 0.5rem;">请点击"执行AI分析"按钮进行分析</div>
            </div>
        </div>
    `;
}

async function startAiAnalysis() {
    const deviceId = document.getElementById('ai-device').value;
    if (!deviceId) {
        alert('请先选择设备');
        return;
    }

    if (!lastSavedRadarData) {
        alert('请先上传雷达数据，再进行AI分析');
        return;
    }

    showProgress();

    const radarData = {
        edgeDevice: { deviceId: deviceId },
        speed: lastSavedRadarData.speed,
        trajectoryX: lastSavedRadarData.trajectoryX,
        trajectoryY: lastSavedRadarData.trajectoryY,
        rawData: lastSavedRadarData.rawData
    };

    try {
        const response = await fetch(`${BASE_URL}/api/ai/test`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(radarData)
        });

        const result = await response.json();

        hideProgress();

        if (result.code === 200) {
            const aiResult = result.data || result.message || '';
            console.log('AI分析结果:', aiResult);
            displayAiAnalysisResult(aiResult, deviceId, radarData.speed, radarData.trajectoryX, radarData.trajectoryY);
        } else {
            displayErrorResult('AI分析失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        hideProgress();
        displayErrorResult('AI分析失败: ' + error.message);
    }
}

function displayAiAnalysisResult(analysisResult, deviceId, speed, trajectoryx, trajectoryy) {
    const resultContent = document.getElementById('ai-result-content');
    const resultStatus = document.getElementById('result-status');

    let parsedResult = null;

    try {
        if (typeof analysisResult === 'string') {
            if (analysisResult.startsWith('{') && analysisResult.endsWith('}')) {
                parsedResult = JSON.parse(analysisResult);
            } else {
                const jsonMatch = analysisResult.match(/\{[\s\S]*\}/);
                if (jsonMatch) {
                    parsedResult = JSON.parse(jsonMatch[0]);
                }
            }
        } else if (typeof analysisResult === 'object') {
            parsedResult = analysisResult;
        }
    } catch (e) {
        console.log('解析AI结果JSON失败:', e);
        console.log('原始分析结果:', analysisResult);
    }

    if (parsedResult && parsedResult.riskLevel) {
        const riskLevel = parsedResult.riskLevel.toUpperCase();
        const riskClass = riskLevel === 'HIGH' ? 'risk-high' : (riskLevel === 'MEDIUM' ? 'risk-medium' : 'risk-low');
        const statusClass = riskLevel === 'HIGH' ? 'status-pending' : (riskLevel === 'MEDIUM' ? 'status-confirmed' : 'status-resolved');
        const statusText = riskLevel === 'HIGH' ? '高风险' : (riskLevel === 'MEDIUM' ? '中风险' : '低风险');

        resultStatus.className = `status-badge ${statusClass}`;
        resultStatus.textContent = statusText;

        const riskColor = getRiskColor(riskLevel);

        resultContent.innerHTML = `
            <div class="ai-result-card ${riskClass} ${riskLevel === 'HIGH' ? 'fall-detection-alert' : ''}">
                <div class="ai-result-header">
                    <span class="ai-result-icon">${riskLevel === 'HIGH' ? '🚨' : (riskLevel === 'MEDIUM' ? '⚠️' : '✅')}</span>
                    <span class="ai-result-title">AI跌倒风险分析结果</span>
                </div>
                <div class="ai-result-grid">
                    <div class="ai-result-item">
                        <div class="ai-result-item-label">📊 设备ID</div>
                        <div class="ai-result-item-value">${deviceId}</div>
                    </div>
                    <div class="ai-result-item">
                        <div class="ai-result-item-label">⚡ 速度</div>
                        <div class="ai-result-item-value">${speed} m/s</div>
                    </div>
                    <div class="ai-result-item">
                        <div class="ai-result-item-label">📈 轨迹X</div>
                        <div class="ai-result-item-value">${trajectoryx}</div>
                    </div>
                    <div class="ai-result-item">
                        <div class="ai-result-item-label">📉 轨迹Y</div>
                        <div class="ai-result-item-value">${trajectoryy}</div>
                    </div>
                </div>
                <div class="ai-result-risk ${riskLevel === 'HIGH' ? 'high' : (riskLevel === 'MEDIUM' ? 'medium' : 'low')}" style="margin-top: 1rem;">
                    <div class="ai-result-risk-title" style="color: ${riskColor};">🎯 风险等级: ${riskLevel}</div>
                    <div class="ai-result-risk-value">分析结论: ${parsedResult.conclusion || '-'}</div>
                </div>
                <div class="ai-analysis-section">
                    <div class="ai-analysis-label">📝 判断理由</div>
                    <div class="ai-analysis-content">${parsedResult.reason || '-'}</div>
                </div>
                <div class="ai-analysis-section">
                    <div class="ai-analysis-label">💬 处理建议</div>
                    <div class="ai-analysis-content">${parsedResult.suggestion || '-'}</div>
                </div>
                ${riskLevel === 'HIGH' ? `
                <div style="background: #ffebee; padding: 1rem; border-radius: 8px; margin-top: 1rem; border: 2px solid #ef9a9a;">
                    <div style="color: #c62828; font-weight: bold; margin-bottom: 0.5rem;">⚠️ 危险告警</div>
                    <div style="color: #333; margin-bottom: 1rem;">系统检测到高风险跌倒情况，建议立即上报告警</div>
                    <button class="btn btn-danger" onclick="reportFallAlert('${deviceId}')" style="width: 100%;">
                        🚨 立即上报告警
                    </button>
                </div>
                ` : ''}
                <div style="color: #888; font-size: 12px; text-align: center; margin-top: 10px;">
                    (调用GLM-4-Flash模型进行跌倒风险分析)
                </div>
            </div>
        `;
    } else {
        const isFall = analysisResult.toLowerCase().includes('fall') || analysisResult.toLowerCase().includes('跌倒');
        const riskClass = isFall ? 'risk-high' : 'risk-low';
        const statusClass = isFall ? 'status-pending' : 'status-resolved';
        const statusText = isFall ? '高风险' : '分析完成';

        resultStatus.className = `status-badge ${statusClass}`;
        resultStatus.textContent = statusText;

        resultContent.innerHTML = `
            <div class="ai-result-card ${riskClass}">
                <div class="ai-result-header">
                    <span class="ai-result-icon">🤖</span>
                    <span class="ai-result-title">AI分析结果</span>
                </div>
                <div class="ai-result-grid">
                    <div class="ai-result-item">
                        <div class="ai-result-item-label">📊 设备ID</div>
                        <div class="ai-result-item-value">${deviceId}</div>
                    </div>
                    <div class="ai-result-item">
                        <div class="ai-result-item-label">⚡ 速度</div>
                        <div class="ai-result-item-value">${speed} m/s</div>
                    </div>
                </div>
                <div class="ai-result-risk ${isFall ? 'high' : 'low'}" style="margin-top: 1rem;">
                    <div class="ai-result-risk-title">💡 分析结果</div>
                    <div class="ai-result-risk-value" style="white-space: pre-wrap;">${analysisResult}</div>
                </div>
                ${isFall ? `
                <div style="background: #ffebee; padding: 1rem; border-radius: 8px; margin-top: 1rem;">
                    <button class="btn btn-danger" onclick="reportFallAlert('${deviceId}')" style="width: 100%;">
                        🚨 立即上报告警
                    </button>
                </div>
                ` : ''}
            </div>
        `;
    }
}

async function reportFallAlert(deviceId) {
    if (!confirm('确定要上报警告吗？这将通知家属和医护人员。')) {
        return;
    }

    const alertRequest = {
        deviceId: deviceId,
        alertType: 'FALL'
    };

    try {
        const response = await fetch(`${BASE_URL}/api/alert/report`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(alertRequest)
        });

        const result = await response.json();

        if (result.code === 200) {
            alert('告警已上报成功！系统将通知家属和医护人员。');
            lastSavedRadarData = null;
            clearAiResult();
        } else {
            alert('告警上报失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        alert('告警上报失败: ' + error.message);
    }
}

function displayErrorResult(error) {
    const resultContent = document.getElementById('ai-result-content');
    const resultStatus = document.getElementById('result-status');

    resultStatus.className = 'status-badge status-pending';
    resultStatus.textContent = '分析失败';

    resultContent.innerHTML = `
        <div style="text-align: center; padding: 2rem;">
            <div style="font-size: 3rem; margin-bottom: 1rem;">❌</div>
            <div style="color: #c62828; font-weight: 600; margin-bottom: 0.5rem;">分析失败</div>
            <div style="color: #636e72; font-size: 0.9rem;">${error}</div>
        </div>
    `;
}

async function simulateFallDetection() {
    const deviceId = document.getElementById('ai-device').value;
    if (!deviceId) {
        alert('请先选择设备');
        return;
    }

    showProgress();

    const alertRequest = {
        deviceId: deviceId,
        alertType: 'FALL'
    };

    try {
        const response = await fetch(`${BASE_URL}/api/alert/report`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(alertRequest)
        });

        const result = await response.json();

        hideProgress();

        if (result.code === 200) {
            displayFallResult(deviceId, result.data);
        } else {
            displayErrorResult('跌倒检测失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        hideProgress();
        displayErrorResult('跌倒检测失败: ' + error.message);
    }
}

function displayFallResult(deviceId, eventData) {
    const resultContent = document.getElementById('ai-result-content');
    const resultStatus = document.getElementById('result-status');

    resultStatus.className = 'status-badge status-pending';
    resultStatus.textContent = '高风险';

    resultContent.innerHTML = `
        <div class="ai-result-card risk-high fall-detection-alert">
            <div class="ai-result-header">
                <span class="ai-result-icon">🚨</span>
                <span class="ai-result-title">跌倒检测告警</span>
            </div>
            <div class="ai-result-grid">
                <div class="ai-result-item">
                    <div class="ai-result-item-label">📊 设备ID</div>
                    <div class="ai-result-item-value">${deviceId}</div>
                </div>
                <div class="ai-result-item">
                    <div class="ai-result-item-label">⚡ 检测速度</div>
                    <div class="ai-result-item-value">4.5 m/s</div>
                </div>
                <div class="ai-result-item">
                    <div class="ai-result-item-label">✅ 置信度</div>
                    <div class="ai-result-item-value">98%</div>
                </div>
            </div>
            <div class="ai-result-risk high" style="margin-top: 1rem;">
                <div class="ai-result-risk-title">⚠️ 检测结果</div>
                <div class="ai-result-risk-value" style="color: #c62828;">检测到跌倒事件！</div>
            </div>
            <div style="background: rgba(255,255,255,0.8); padding: 1rem; border-radius: 8px; margin-top: 1rem;">
                <div style="color: #666; font-size: 0.8rem; margin-bottom: 0.5rem;">📋 自动处理流程</div>
                <div style="color: #333; font-size: 0.9rem; line-height: 1.6;">
                    1. 📢 立即发送告警通知给家属<br>
                    2. 🏥 联系附近医疗机构<br>
                    3. 👨‍⚕️ 通知值班医护人员前往查看<br>
                    4. 📝 记录事件详情到系统日志
                </div>
            </div>
            ${eventData?.id ? `
            <div style="color: #888; font-size: 12px; margin-top: 10px;">
                事件ID: ${eventData.id} | 时间: ${formatDate(eventData.eventTime)}
            </div>
            ` : ''}
            <div style="margin-top: 10px; padding: 1rem; background: #fff; border-radius: 5px; text-align: center;">
                <span style="color: #c62828;">💡</span>
                <span style="color: #666;">已生成跌倒告警，请切换到</span>
                <button style="background: #c62828; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer; margin: 0 5px;" onclick="showPage('alerts')">告警中心</button>
                <span style="color: #666;">查看详情</span>
            </div>
        </div>
    `;
}

function searchDevices() {
    const searchTerm = document.getElementById('device-search').value.toLowerCase();
    const table = document.getElementById('device-list').querySelector('table');
    if (!table) return;

    const rows = table.querySelectorAll('tbody tr');
    rows.forEach(row => {
        const deviceId = row.querySelector('td:first-child').textContent.toLowerCase();
        const location = row.querySelector('td:nth-child(2)').textContent.toLowerCase();
        row.style.display = (deviceId.includes(searchTerm) || location.includes(searchTerm)) ? '' : 'none';
    });
}

function searchUsers() {
    const searchTerm = document.getElementById('user-search').value.toLowerCase();
    const table = document.getElementById('user-list').querySelector('table');
    if (!table) return;

    const rows = table.querySelectorAll('tbody tr');
    rows.forEach(row => {
        const username = row.querySelector('td:first-child').textContent.toLowerCase();
        const name = row.querySelector('td:nth-child(2)').textContent.toLowerCase();
        row.style.display = (username.includes(searchTerm) || name.includes(searchTerm)) ? '' : 'none';
    });
}

window.onclick = function(event) {
    const modals = document.querySelectorAll('.modal');
    modals.forEach(modal => {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });
};

document.addEventListener('DOMContentLoaded', function() {
    showPage('login-page');
});

function isTokenValid(token) {
    try {
        const payload = token.split('.')[1];
        const decoded = atob(payload);
        const data = JSON.parse(decoded);
        const exp = data.exp * 1000;
        return Date.now() < exp;
    } catch {
        return false;
    }
}