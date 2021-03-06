package ru.job4j.dream.store;

import org.apache.commons.dbcp2.BasicDataSource;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;
import ru.job4j.dream.model.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PsqlStore implements Store {
    private final BasicDataSource pool = new BasicDataSource();

    private PsqlStore() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(new FileReader("db.properties"))) {
            cfg.load(io);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
        createTable();
        addUser(new User("Admin", "root@local", "root"));
        addCity("Moscow");
        addCity("SPB");
    }

    private static final class Lazy {
        private static final Store INST = new PsqlStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    private void createTable() {
        String sql = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader("C:\\projects\\job4j_dreamjob_new_version\\db\\schema.sql"));
            int c;
            while ((c = reader.read()) != -1) {
                sql += (char) c;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<Post> findAllPosts() {
        List<Post> posts = new ArrayList<>();
        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement("Select * from post")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    posts.add(new Post(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            parseDate(rs.getString("date"))));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Collection<Candidate> findAllCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement("Select * from candidate")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    candidates.add(new Candidate(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("city_id"),
                            rs.getString("photoId")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return candidates;
    }

    @Override
    public void save(Post post) {
        if (post.getId() == 0) {
            create(post);
        } else {
            update(post);
        }
    }

    @Override
    public void save(Candidate candidate) {
        if (candidate.getId() == 0) {
            create(candidate);
        } else {
            update(candidate);
        }
    }

    private Post create(Post post) {
        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "Insert into post (name, description, date) values (?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getName());
            ps.setString(2, post.getDescription());
            ps.setString(3, post.getCreate().toString());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    post.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    private void addCity(String city) {
        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "Insert into cities (name) values (?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, city);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Candidate create(Candidate candidate) {
        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "Insert into candidate (name, city_id, photoId) values (?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, candidate.getName());
            ps.setInt(2, candidate.getCity());
            ps.setString(3, candidate.getPhotoId());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    candidate.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return candidate;
    }

    @Override
    public void update(Post post) {
        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE post " +
                             "set name = ?," +
                             "description = ?," +
                             "date = ? " +
                             "where id =" + post.getId())) {
            ps.setString(1, post.getName());
            ps.setString(2, post.getDescription());
            ps.setString(3, post.getCreate().toString());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Candidate candidate) {
        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE candidate " +
                             "set name = ?" +
                             "set city_id = ?" +
                             "photoId = ?" +
                             "where id =" + candidate.getId())) {
            ps.setString(1, candidate.getName());
            ps.setInt(2, candidate.getCity());
            ps.setString(3, candidate.getPhotoId());
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Post findById(int id) {
        Post result = null;
        List<Post> list = (List<Post>) findAllPosts();
        for (Post post : list) {
            if (post.getId() == id) {
                result = post;
            }
        }
        return result;
    }

    @Override
    public Candidate findByIdCandidate(int id) {
        Candidate result = null;
        List<Candidate> list = (List<Candidate>) findAllCandidates();
        for (Candidate candidate : list) {
            if (candidate.getId() == id) {
                result = candidate;
            }
        }
        return result;
    }

    private Date parseDate(String date) {
        Date result = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            result = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void deleteCandidate(int id) {
        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE from candidate where id=" + id)) {
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addUser(User user) {
        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "Insert into users (name, email, password) values (?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    user.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<User> findAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement("Select * from users")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public User findUserByEmail(String email) {
        User result = null;
        for (User u : findAllUsers()) {
            if (u.getEmail().equals(email)) {
                result = u;
            }
        }
        return result;
    }

    @Override
    public List<String> findAllCities() {
        List<String> list = new ArrayList<>();
        try (Connection connection = pool.getConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("select * from cities")) {
            while (rs.next()) {
                list.add(rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int getIdCity(String city) {
        int result = 0;
        try (Connection connection = pool.getConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("select * from cities")) {
            while (rs.next()) {
                if (rs.getString("name").equals(city)) {
                    result = rs.getInt("id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String getCity(int id) {
        String result = null;
        try (Connection connection = pool.getConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("select * from cities")) {
            while (rs.next()) {
                if (rs.getInt("id") == id) {
                    result = rs.getString("name");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
