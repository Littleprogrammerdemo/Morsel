    let stompClient = null;
    let username = null;

    // Connect to WebSocket
    function connect() {
        let socket = new SockJS('/chat');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/messages', function (message) {
                showMessage(JSON.parse(message.body));
            });
            loadChatHistory();
        });
    }

    // Send message
    function sendMessage() {
        if (!username) {
            username = document.getElementById("username").value.trim();
            if (!username) {
                alert("Enter your name first!");
                return;
            }
        }

        let content = document.getElementById("message").value.trim();
        if (content) {
            let chatMessage = { sender: username, content: content };
            stompClient.send("/app/sendMessage", {}, JSON.stringify(chatMessage));
            document.getElementById("message").value = "";
        }
    }

    // Show received messages
    function showMessage(message) {
        let messageDiv = document.createElement("div");
        messageDiv.classList.add("message", message.sender === username ? "own-message" : "other-message");
        messageDiv.innerHTML = `<strong>${message.sender}:</strong> ${message.content}`;
        document.getElementById("messages").appendChild(messageDiv);
    }

    // Load chat history
    function loadChatHistory() {
        fetch('/chat/history')
            .then(response => response.json())
            .then(messages => messages.forEach(showMessage));
    }

    // Connect when page loads
    window.onload = connect;
