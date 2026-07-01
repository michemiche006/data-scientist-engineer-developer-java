package com.example.aiecosystem.appdev;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class ChatController {

    private final RagChatAgent chatAgent;

    public ChatController(RagChatAgent chatAgent) {
        this.chatAgent = chatAgent;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "ok",
                "agentReady", chatAgent.isReady(),
                "vectorCount", chatAgent.vectorCount(),
                "timestamp", Instant.now()
        );
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody ChatDtos.ChatRequest request) {
        if (!chatAgent.isReady()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                    "error", "Chat agent not ready",
                    "message", "Run Exercise 1 and Exercise 2 before starting the server"
            ));
        }
        if (request == null || request.message() == null || request.message().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid request",
                    "message", "Message is required and must be a non-empty string"
            ));
        }

        try {
            ChatDtos.ChatResult result = chatAgent.chat(request.message(), request.conversationId());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", result,
                    "timestamp", Instant.now()
            ));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Internal server error",
                    "message", "Failed to process chat request",
                    "details", ex.getMessage()
            ));
        }
    }

    @GetMapping("/conversations/{conversationId}")
    public Map<String, Object> conversationHistory(@PathVariable String conversationId) {
        return Map.of(
                "success", true,
                "conversationId", conversationId,
                "history", chatAgent.getConversationHistory(conversationId),
                "timestamp", Instant.now()
        );
    }

    @GetMapping("/")
    public String index() {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>RAG Chat Agent - Java</title>
                    <style>
                        body { font-family: Arial, sans-serif; max-width: 800px; margin: 0 auto; padding: 20px; }
                        .chat-container { border: 1px solid #ddd; height: 400px; overflow-y: auto; padding: 10px; margin: 10px 0; }
                        .message { margin: 10px 0; padding: 10px; border-radius: 5px; }
                        .user-message { background-color: #e3f2fd; text-align: right; }
                        .agent-message { background-color: #f5f5f5; }
                        .input-container { display: flex; gap: 10px; }
                        .message-input { flex: 1; padding: 10px; }
                        .send-button { padding: 10px 20px; }
                    </style>
                </head>
                <body>
                    <h1>RAG Chat Agent - Java + LangChain4j</h1>
                    <div id="status">Checking status...</div>
                    <div class="chat-container" id="chatContainer">
                        <div class="message agent-message"><strong>Assistant:</strong> Ask me about the processed AI/ML content.</div>
                    </div>
                    <div class="input-container">
                        <input id="messageInput" class="message-input" placeholder="Ask about AI, Bedrock, or LangChain4j..." onkeypress="if(event.key==='Enter') sendMessage()">
                        <button class="send-button" onclick="sendMessage()">Send</button>
                    </div>
                    <script>
                        let conversationId = null;
                        fetch('/health').then(r => r.json()).then(data => {
                            document.getElementById('status').textContent = data.agentReady
                                ? `Agent ready (${data.vectorCount} vectors)`
                                : 'Agent not ready. Run Exercise 1 and Exercise 2 first.';
                        });
                        function addMessage(sender, text, className) {
                            const chat = document.getElementById('chatContainer');
                            const div = document.createElement('div');
                            div.className = 'message ' + className;
                            div.innerHTML = `<strong>${sender}:</strong> ${text}`;
                            chat.appendChild(div);
                            chat.scrollTop = chat.scrollHeight;
                        }
                        async function sendMessage() {
                            const input = document.getElementById('messageInput');
                            const message = input.value.trim();
                            if (!message) return;
                            addMessage('You', message, 'user-message');
                            input.value = '';
                            const response = await fetch('/chat', {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify({ message, conversationId })
                            });
                            const data = await response.json();
                            if (data.success) {
                                conversationId = data.data.conversationId;
                                const sources = data.data.sources.map(s => s.title).join(', ');
                                addMessage('Assistant', data.data.response + (sources ? `<br><small>Sources: ${sources}</small>` : ''), 'agent-message');
                            } else {
                                addMessage('Assistant', data.message || data.error, 'agent-message');
                            }
                        }
                    </script>
                </body>
                </html>
                """;
    }
}
