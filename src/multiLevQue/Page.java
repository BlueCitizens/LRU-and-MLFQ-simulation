package multiLevQue;


import java.util.TreeMap;

public class Page {
    private int num;
    private String content;

    public Page() {
    }

    public Page(int num) {
        this.num = num;
    }

    public Page(int num, String content) {
        this.num = num;
        this.content = content;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Page{" +
                "num=" + num +
                ", content='" + content + '\'' +
                '}';
    }
}
