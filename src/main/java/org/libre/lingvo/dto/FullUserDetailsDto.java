package org.libre.lingvo.dto;

/**
 * Created by igorek2312 on 10.09.16.
 */
public class FullUserDetailsDto {
    private long id;

    private String email;

    private String name;

    private int translationsCount;

    private boolean enabled;

    private boolean nonLocked;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTranslationsCount() {
        return translationsCount;
    }

    public void setTranslationsCount(int translationsCount) {
        this.translationsCount = translationsCount;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isNonLocked() {
        return nonLocked;
    }

    public void setNonLocked(boolean nonLocked) {
        this.nonLocked = nonLocked;
    }
}
