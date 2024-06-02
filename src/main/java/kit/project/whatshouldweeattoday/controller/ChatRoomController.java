package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.dto.chat.ChatRoomDTO;
import kit.project.whatshouldweeattoday.domain.dto.chat.RoomAndFriendsRequestDTO;
import kit.project.whatshouldweeattoday.domain.entity.ChatRoom;
import kit.project.whatshouldweeattoday.domain.entity.ChatRoomMember;
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
            Member creator = memberService.findByLoginId(SecurityUtil.getLoginId())
                    .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));
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
        List<ChatRoom> chatRooms = chatRoomService.findAllRoom();
        List<ChatRoomDTO> includeChatRooms = new ArrayList<>();
        String loginId = SecurityUtil.getLoginId();
        System.out.println("현재 로그인된 사용자 ID: " + loginId);

        for (ChatRoom room : chatRooms) {
            Set<ChatRoomMember> participants = room.getChatRoomMembers();
            if (participants == null || participants.isEmpty()) {
                System.out.println("참여자 목록이 비어 있습니다. 채팅방 ID: " + room.getId());
            } else {
                System.out.println("채팅방 ID: " + room.getId() + ", 참여자 수: " + participants.size());
                for (ChatRoomMember participant : participants) {
                    System.out.println("참여자 ID: " + participant.getMember().getLoginId());
                    if (Objects.equals(participant.getMember().getLoginId(), loginId)) {
                        includeChatRooms.add(ChatRoomDTO.from(room, loginId));
                        System.out.println("추가된 채팅방 ID: " + room.getId());
                        break;
                    }
                }
            }
        }

        System.out.println("총 포함된 채팅방 수: " + includeChatRooms.size());
        return new ResponseEntity<>(includeChatRooms, HttpStatus.OK);
    }
}