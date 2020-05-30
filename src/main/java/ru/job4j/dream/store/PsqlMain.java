package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;

import java.util.Date;

public class PsqlMain {
    public static void main(String[] args) {
        Store store = PsqlStore.instOf();
        store.save(new Post(0, "Java Job", "Java Job description", new Date()));
        store.save(new Candidate(0, "Candidate JAva", 0, null));
        for (Post post : store.findAllPosts()) {
            System.out.println(post.getId() + " " + post.getName() + " " + post.getDescription() + " " + post.getCreate());
        }
        for (Candidate candidate : store.findAllCandidates()) {
            System.out.println(candidate.getId() + " " + candidate.getName());
        }
        System.out.println(store.findById(1).getName());
        System.out.println(store.findByIdCandidate(1).getName());
        store.update(new Post(1, "Java updateJob", "Java updateDesc", new Date()));
        store.update(new Candidate(1, "Candidate Java update", 0, null));
        System.out.println(store.findById(1).getName());
        System.out.println(store.findByIdCandidate(1).getName());
    }
}
