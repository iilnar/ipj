package chat;

public class User {
    private String alias;
    private int passwordHash;

    public User(String alias, String password) {
        this.alias = alias;
        this.passwordHash = password.hashCode();
    }

    public String getAlias() {
        return alias;
    }

    public int getPasswordHash() {
        return passwordHash;
    }

    public void setPassword(String password) {
        this.passwordHash = password.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return alias != null ? alias.equals(user.alias) : user.alias == null;
    }

    @Override
    public int hashCode() {
        return alias != null ? alias.hashCode() : 0;
    }
}
