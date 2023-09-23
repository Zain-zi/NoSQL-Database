package com.example.NoSQLDecentralizedDatabase.controllers;

import com.example.NoSQLDecentralizedDatabase.models.User;
import com.example.NoSQLDecentralizedDatabase.services.BootstrapperNodeService;
import com.example.NoSQLDecentralizedDatabase.services.AuthenticationService;
import com.example.NoSQLDecentralizedDatabase.services.NodeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthenticationController {
    @GetMapping("/login")
    public String getLoginPage(@RequestParam(value = "nodeIP", required = false) String nodeIP, Model model) {
        if (!NodeService.isBootstrap()) {
            System.out.println("BAD REQUEST");
            return null;
        }
        if (nodeIP != null) {
            model.addAttribute("nodeIP", nodeIP);
        }
        return "login";
    }
    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam("nodeIP") String nodeIP,
                        HttpSession session) {
        if (!NodeService.isBootstrap()) {
            System.out.println("BAD REQUEST");
            return null;
        }
        User user = AuthenticationService.findUser(username, password, nodeIP);
        if (user != null) {
            System.out.println("Login successful.");
            String role = String.valueOf(user.getRole());
            session.setAttribute("role", role);
            if (role.equals("SYSTEM_ADMIN")) {
                return "redirect:/database";
            } else {
                return "redirect:/documents";
            }
        } else {
            System.out.println("Login unsuccessful.");
            System.out.println("BAD REQUEST");
        }
        return null;
    }
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }
    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam("confirmPassword") String confirmPassword,
                           @RequestParam("email") String email,
                           RedirectAttributes redirectAttributes) {
        if (!NodeService.isBootstrap()) {
            System.out.println("BAD REQUEST");
        }
        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords mismatch.");
            return "redirect:/register";
        }
        String nodeIP = BootstrapperNodeService.createUser(username, email, password);
        if (nodeIP != null) {
            System.out.println("Registration successful.");
            redirectAttributes.addAttribute("nodeIP", nodeIP);
        } else {
            System.out.println("Registration unsuccessful.");
        }
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
