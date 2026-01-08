package entities.tickets;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Comment {
    private String author;
    private String content;
    private String createdAt;

    public Comment(String author, String content, String createdAt) {
        this.author = author;
        this.content = content;
        this.createdAt = createdAt;
    }
}
