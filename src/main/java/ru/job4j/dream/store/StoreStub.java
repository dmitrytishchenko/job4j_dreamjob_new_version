package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;
import ru.job4j.dream.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreStub implements Store {
    private final Map<Integer, Candidate> candidateMap = new HashMap<>();
    private final Map<Integer, Post> postMap = new HashMap<>();
    private final Map<Integer, User> userMap = new HashMap<>();
    private int idsCandidate = 0;
    private int idsPost = 0;
    private int idsUser = 0;

    @Override
    public Collection<Post> findAllPosts() {
        return this.postMap.values();
    }

    @Override
    public Collection<Candidate> findAllCandidates() {
        return this.candidateMap.values();
    }

    @Override
    public void save(Post post) {
        post.setId(this.idsPost);
        this.postMap.put(post.getId(), post);
    }

    @Override
    public void save(Candidate candidate) {
        candidate.setId(this.idsCandidate);
        this.candidateMap.put(candidate.getId(), candidate);
    }

    @Override
    public Post findById(int id) {
        return this.postMap.get(id);
    }

    @Override
    public Candidate findByIdCandidate(int id) {
        return this.candidateMap.get(id);
    }

    @Override
    public void update(Post post) {
        this.postMap.get(post.getId()).setName(post.getName());
        this.postMap.get(post.getId()).setDescription(post.getDescription());
        this.postMap.get(post.getId()).setCreate(post.getCreate());
    }

    @Override
    public void update(Candidate candidate) {
        this.candidateMap.get(candidate.getId()).setName(candidate.getName());
    }

    @Override
    public void deleteCandidate(int id) {
        this.candidateMap.remove(id);
    }

    @Override
    public void addUser(User user) {
        user.setId(this.idsUser++);
        this.userMap.put(user.getId(), user);
    }

    @Override
    public Collection<User> findAllUsers() {
        return this.userMap.values();
    }

    @Override
    public User findUserByEmail(String email) {
        User result = null;
        for (User user : this.userMap.values()) {
            if(user.getEmail().equals(email)) {
                result = user;
            }
        }
        return result;
    }

    @Override
    public List<String> findAllCities() {
        return null;
    }

    @Override
    public int getIdCity(String city) {
        return 0;
    }

    @Override
    public String getCity(int id) {
        return null;
    }

}
