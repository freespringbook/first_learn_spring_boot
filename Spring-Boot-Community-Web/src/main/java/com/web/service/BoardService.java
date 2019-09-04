package com.web.service;

import com.web.domain.Board;
import com.web.repository.BoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Created by freejava1191@gmail.com on 2019-08-28
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    /**
     * pageable로 넘어온 pageNumber 객체가 0 이하일 때 0으로 초기화
     * 기본 페이지 크기인 10으로 새로운 PageRequest 객체를 만들어 페이징 처리된 게시글 리스트 반환
     * @param pageable
     * @return
     */
   public Page<Board> findBoardList(Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1, pageable.getPageSize());
        // 스프링 부트 1.5 버전 대응 로직
        // pageable = new PageRequest(pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1, pageable.getPageSize());
        return boardRepository.findAll(pageable);
   }

   public Board findBoardByIdx(Long idx) {
        return boardRepository.findById(idx).orElse(new Board());
   }

}
