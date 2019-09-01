package com.web.repository;

import com.web.domain.Board;
import com.web.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by freejava1191@gmail.com on 2019-08-27
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
public interface BoardRepository extends JpaRepository<Board, Long> {
    Board findByUser(User user);
}
