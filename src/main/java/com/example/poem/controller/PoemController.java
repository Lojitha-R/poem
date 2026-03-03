package com.example.poem.controller;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.poem.model.Poem;
import com.example.poem.model.User;
import com.example.poem.repository.PoemRepository;
import com.example.poem.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class PoemController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PoemRepository poemRepo;

    // ================= START PAGE =================
    @GetMapping("/")
    public String start() {
        return "index";
    }

    // ================= LOGIN PAGE =================
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // ================= REGISTER PAGE =================
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // ================= REGISTER USER =================
    @PostMapping("/registerUser")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String confirmPassword,
                               @RequestParam(required = false) String phone) {

        if (!password.equals(confirmPassword)) {
            return "register";
        }

        if (userRepo.findByUsername(username) != null) {
            return "register";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setPhone(phone);

        userRepo.save(user);

        return "login";
    }

    // ================= LOGIN USER =================
    @PostMapping("/loginUser")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            HttpSession session) {

        User user = userRepo.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("user", user);
            return "redirect:/home";
        }

        return "login";
    }

    // ================= HOME PAGE =================
    @GetMapping("/home")
    public String home(Model model, HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        List<Poem> poems = poemRepo.findByUser(user);
        model.addAttribute("poems", poems);

        return "home";
    }

    // ================= WRITE PAGE =================
    @GetMapping("/write")
    public String writePage(HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        return "write";
    }

    // ================= SAVE POEM (DB + TEXT FILE) =================
    @PostMapping("/save")
    public String savePoem(@RequestParam String title,
                           @RequestParam String poem,
                           HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        // Save to database
        Poem p = new Poem();
        p.setTitle(title);
        p.setContent(poem);
        p.setUser(user);

        poemRepo.save(p);

        // Save as text file
        try {
            String folderPath = "poems";

            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdir();
            }

            // Safe file name
            String fileName = title.replaceAll("[^a-zA-Z0-9]", "_") + ".txt";

            File file = new File(folderPath + "/" + fileName);

            FileWriter writer = new FileWriter(file);
            writer.write("Title: " + title + "\n");
            writer.write("Author: " + user.getUsername() + "\n\n");
            writer.write(poem);
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/success";
    }

    // ================= SUCCESS PAGE =================
    @GetMapping("/success")
    public String successPage(HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        return "success";
    }

    // ================= LOGOUT =================
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}