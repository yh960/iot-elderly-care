package com.iot.elderly.elderlycarebackend.entity;

import javax.persistence.*;

/**
 * 用户实体 —— 对应数据库 user 表
 *
 * 系统中有三种用户角色（通过 userType 区分）：
 * - ELDERLY: 被监测的老人（设备绑定的对象）
 * - FAMILY: 老人的家属（接收报警通知）
 * - ADMIN: 管理员
 *
 * 字段说明：
 * - id: 自增主键
 * - name: 真实姓名
 * - username: 登录用户名（唯一）
 * - password: 登录密码（BCrypt 加密存储）
 * - phone: 手机号
 * - familyPhone: 家属联系电话（紧急联系用）
 * - address: 家庭地址
 * - age: 年龄
 * - email: 邮箱
 * - gender: 性别
 * - userType: 用户类型（"elderly"/"family"/"admin"）
 * - openid: 【废案】微信小程序的 openid，用于微信登录，项目放弃微信小程序后不再使用
 */
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "family_phone", nullable = false, length = 20)
    private String familyPhone;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "age")
    private Integer age;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "user_type", length = 20)
    private String userType;

    @Column(name = "username", length = 50)
    private String username;

    @Column(name = "openid", length = 255)
    private String openid;

    // Getter 和 Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFamilyPhone() { return familyPhone; }
    public void setFamilyPhone(String familyPhone) { this.familyPhone = familyPhone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getOpenid() { return openid; }
    public void setOpenid(String openid) { this.openid = openid; }
}
