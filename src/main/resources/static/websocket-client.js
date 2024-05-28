let stompClient = null;

function connect() {
    let socket = new SockJS('/ws-stomp');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/public', function (message) {
            showResponse(JSON.parse(message.body));
        });
    });
}

function createRoomAndInviteFriends() {
    const roomName = document.getElementById('roomName').value;
    const friendLoginIds = document.getElementById('friendLoginIds').value.split(',');

    stompClient.send("/app/chat.createRoomAndInviteFriends", {}, JSON.stringify({
        name: roomName,
        friendLoginIds: friendLoginIds
    }));
}

function showResponse(message) {
    document.getElementById('response').innerHTML = `<p>Room Created: ${message.name} (ID: ${message.id})</p>`;
}

window.onload = function () {
    connect();
};

// <!DOCTYPE html>
// <html xmlns:th="http://www.thymeleaf.org">
// <head>
//     <title>Hello WebSocket</title>
//     <link href="/webjars/bootstrap/css/bootstrap.min.css" rel="stylesheet">
//     <link href="/main.css" rel="stylesheet">
//     <script src="/webjars/jquery/jquery.min.js"></script>
//     <script src="/webjars/sockjs-client/sockjs.min.js"></script>
//     <script src="/webjars/stomp-websocket/stomp.min.js"></script>
// </head>
// <body>
// <noscript><h2 style="color: #ff0000">Seems your browser doesn't support Javascript! Websocket relies on Javascript being
//     enabled. Please enable
//     Javascript and reload this page!</h2></noscript>
// <div id="main-content" class="container">
//     <div class="row">
//         <div class="col-md-6">
//             <form class="form-inline">
//                 <div class="form-group">
//                     <label for="connect">WebSocket connection:</label>
//                     <button id="connect" class="btn btn-default" type="submit">Connect</button>
//                     <button id="disconnect" class="btn btn-default" type="submit" disabled="disabled">Disconnect
//                     </button>
//                 </div>
//             </form>
//         </div>
//         <div class="col-md-6">
//             <form class="form-inline">
//                 <div class="form-group">
//                     <label for="name">약속 장</label>
//                     <input type="text" id="sender" class="form-control" placeholder="Your name here...">
//                 </div>
//                 <div class="form-group">
//                     <label for="name">약속 메뉴</label>
//                     <input type="text" id="message" class="form-control" placeholder="메시지 내용">
//                 </div>
//                 <button id="send" class="btn btn-default" type="submit">Send</button>
//             </form>
//         </div>
//     </div>
//     <div class="row">
//         <div class="col-md-12">
//             <table id="conversation" class="table table-striped">
//                 <thead>
//                 <tr>
//                     <th>Chat</th>
//                 </tr>
//                 </thead>
//                 <tbody id="chatting">
//                 </tbody>
//             </table>
//         </div>
//     </div>
// </div>
// </body>
// <script th:inline="javascript">
//     var stompClient = null;
//     var roomId = [[${roomId}]];
//     var chatList = [[${chatList}]];
//
//     function setConnected(connected) {
//         $("#connect").prop("disabled", connected);
//         $("#disconnect").prop("disabled", !connected);
//         if (connected) {
//             $("#conversation").show();
//         }
//         else {
//             $("#conversation").hide();
//         }
//         $("#chatting").html("");
//     }
//
//     function connect() {
//         var socket = new SockJS('/ws-stomp');
//         stompClient = Stomp.over(socket);
//         stompClient.connect({}, function (frame) {
//             setConnected(true);
//             console.log('Connected: ' + frame);
//             loadChat(chatList)  //저장된 채팅 불러오기
//
//             //구독
//             stompClient.subscribe('/room/'+roomId, function (chatMessage) {
//                 showChat(JSON.parse(chatMessage.body));
//             });
//         });
//     }
//
//     function disconnect() {
//         if (stompClient !== null) {
//             stompClient.disconnect();
//         }
//         setConnected(false);
//         console.log("Disconnected");
//     }
//
//     //html 에서 입력값, roomId 를 받아서 Controller 로 전달
//     function sendChat() {
//         stompClient.send("/app/"+roomId, {},
//             JSON.stringify({
//                 'meetLocate' : $("#meetLocate").val(),
//                 'meetMenu' : $("#meetMenu").val()
//             }));
//     }
//
//     //저장된 채팅 불러오기
//     function loadChat(chatList){
//         if(chatList != null) {
//             for(chat in chatList) {
//                 $("#chatting").append(
//                     "<tr><td>" + "[" + chatList[chat].meetLocate + "]" + chatList[chat].meetMenu + "</td></tr>"
//                 );
//             }
//         }
//     }
//
//     //보낸 채팅 보기
//     function showChat(chatMessage) {
//         $("#chatting").append(
//             "<tr><td>" + "[" + chatMessage.meetLocate + "]" + chatMessage.meetMenu + "</td></tr>"
//         );
//     }
//
//     $(function () {
//         $("form").on('submit', function (e) {
//             e.preventDefault();
//         });
//         $( "#connect" ).click(function() { connect(); });
//         $( "#disconnect" ).click(function() { disconnect(); });
//         $( "#send" ).click(function() { sendChat(); });
//     });
//
//
// </script>
// <script>
//     //창 키면 바로 연결
//     window.onload = function (){
//         connect();
//     }
//
//     window.BeforeUnloadEvent = function (){
//         disconnect();
//     }
// </script>
// </html>