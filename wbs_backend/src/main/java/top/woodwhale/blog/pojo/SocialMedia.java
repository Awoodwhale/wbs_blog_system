package top.woodwhale.blog.pojo;

public class SocialMedia {
    String github;
    String bilibili;
    String music163;
    String csdn;
    String mail;

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getBilibili() {
        return bilibili;
    }

    public void setBilibili(String bilibili) {
        this.bilibili = bilibili;
    }

    public String getMusic163() {
        return music163;
    }

    public void setMusic163(String music163) {
        this.music163 = music163;
    }

    public String getCsdn() {
        return csdn;
    }

    public void setCsdn(String csdn) {
        this.csdn = csdn;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public boolean checkIsUrl() {
        if (!(this.bilibili.startsWith("http") && this.bilibili.contains("://"))) {
            return false;
        }
        if (!(this.github.startsWith("http") && this.github.contains("://"))) {
            return false;
        }
        if (!(this.csdn.startsWith("http") && this.csdn.contains("://"))) {
            return false;
        }
        return this.music163.startsWith("http") && this.music163.contains("://");
    }
}
