package com.example.poem.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.poem.model.Poem;
import com.example.poem.model.User;

public interface PoemRepository extends JpaRepository<Poem, Long> {
    List<Poem> findByUser(User user);
}