package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.bookmark.BookmarkRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.bookmark.BookmarkResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.restaurant.RestaurantResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.review.MsgResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.*;
import kit.project.whatshouldweeattoday.repository.BookmarkRepository;
import kit.project.whatshouldweeattoday.repository.MemberRepository;
import kit.project.whatshouldweeattoday.repository.RestaurantRepository;
import kit.project.whatshouldweeattoday.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final RestaurantRepository restaurantRepository;
    private final MemberRepository memberRepository;

    // 즐겨찾기 등록
    @Transactional
    public void save(Long restaurantId,BookmarkRequestDTO bookmarkRequestDTO) {
        String loginId = SecurityUtil.getLoginId();
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(RuntimeException::new);

        Bookmark bookmark = bookmarkRequestDTO.toSaveEntity(member, restaurant);
        bookmarkRepository.save(bookmark);
    }

    // 즐겨찾기 삭제
    @Transactional
    public MsgResponseDTO delete(Long restaurantId, Long bookmarkId) {
        bookmarkRepository.deleteById(bookmarkId);
        return new MsgResponseDTO("즐겨찾기 취소", 200);
    }

    @Transactional
    public Page<BookmarkResponseDTO> findAll(Pageable pageable) {
        String loginId = SecurityUtil.getLoginId();
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));

        Page<Bookmark> bookmarkPage = bookmarkRepository.findAllByMember(member, pageable);
        Page<BookmarkResponseDTO> dtoPage = bookmarkPage.map(BookmarkResponseDTO::new);
        return dtoPage;
    }
}
