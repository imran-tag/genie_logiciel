package fr.uha.ensisa.gl.chatest;

public class ChatStep {
    private Long parentId;
    private Long id;
    private String name;
    private String step;
    private String content;


    public ChatStep() {
    }

    public void setStatus(String status) { this.status = status; }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public void setStep(String step) {
        this.step = step;
    }
    private String status;
    private String comment;

    public String getStatus() {
        return status;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
