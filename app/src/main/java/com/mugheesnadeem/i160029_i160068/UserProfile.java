package com.mugheesnadeem.i160029_i160068;

public class UserProfile {

    String id , Username , ImageURL , search , state ;

    UserProfile(){
        id = "default";
        Username = "default";
        ImageURL = "default";
        search = "default";
        state = "default";
    };

    UserProfile(String id , String Username , String ImageURL, String search, String state)
    {
        this.id = id ;
        this.Username = Username;
        this.ImageURL = ImageURL;
        this.search = search;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
