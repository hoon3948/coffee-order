package kr.spartaclub.coffeeorder.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 비밀번호 해시 생성 유틸리티
 * 실행 후 생성된 해시를 DB에 직접 업데이트하세요
 */
public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("=== Password Hash Generator ===");
        System.out.println();
        
        // admin123 해시 생성
        String adminPassword = "admin123";
        String adminHash = encoder.encode(adminPassword);
        System.out.println("Password: " + adminPassword);
        System.out.println("Hash: " + adminHash);
        System.out.println();
        
        // user123 해시 생성
        String userPassword = "user123";
        String userHash = encoder.encode(userPassword);
        System.out.println("Password: " + userPassword);
        System.out.println("Hash: " + userHash);
        System.out.println();
        
        System.out.println("=== SQL Update Statements ===");
        System.out.println();
        System.out.println("-- Update admin password");
        System.out.println("UPDATE users SET password = '" + adminHash + "' WHERE email = 'admin@coffee.com';");
        System.out.println();
        System.out.println("-- Update user passwords");
        System.out.println("UPDATE users SET password = '" + userHash + "' WHERE email LIKE 'user%@example.com';");
    }
}
