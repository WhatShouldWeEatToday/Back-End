package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.dto.chat.RoomAndFriendsRequestDTO;
import kit.project.whatshouldweeattoday.domain.entity.ChatRoom;
import kit.project.whatshouldweeattoday.domain.entity.Member;
import kit.project.whatshouldweeattoday.security.util.SecurityUtil;
import kit.project.whatshouldweeattoday.service.ChatRoomService;
import kit.project.whatshouldweeattoday.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final MemberService memberService;

    /**
     * 채팅방 생성 및 친구 초대
     * @param requestDTO
     */
   @PostMapping("/room/create")
    public ResponseEntity<ChatRoom> createRoomAndInviteFriends(@RequestBody RoomAndFriendsRequestDTO requestDTO) {
        try {
            Member creator = memberService.findByLoginId(SecurityUtil.getLoginId()).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));
            Set<Member> friends = memberService.findAllByLoginIds(requestDTO.getFriendLoginIds());

            if (friends.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            ChatRoom chatRoom = chatRoomService.createRoomAndInviteFriends(requestDTO.getName(), creator, friends);
            return new ResponseEntity<>(chatRoom, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 내가 속한 채팅방 전체 조회
     */
    @GetMapping("/chat/rooms")
    public ResponseEntity<?> getChatRoom() {
        List<ChatRoom> chatRoom = chatRoomService.findAllRoom();
        List<ChatRoom> includeChatRoom = new ArrayList<>();
        for (ChatRoom room : chatRoom) {
            Set<Member> participants = room.getParticipants();
            for (Member participant : participants) {
                if(Objects.equals(participant.getLoginId(), SecurityUtil.getLoginId())) {
                    includeChatRoom.add(room);
                }
            }
        }
        return new ResponseEntity<>(includeChatRoom, HttpStatus.OK);
    }
}