package com.havi.repository;

import com.havi.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by freejava1191@gmail.com on 2019-08-25
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
public interface BookRepository extends JpaRepository<Book, Integer> {
}
