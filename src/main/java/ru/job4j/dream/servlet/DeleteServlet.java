package ru.job4j.dream.servlet;

import org.apache.commons.fileupload.FileItem;
import ru.job4j.dream.store.PsqlStore;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@WebServlet("/delete")
public class DeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("candidates", PsqlStore.instOf().findAllCandidates());
        req.getRequestDispatcher("candidate/deleteCandidate.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.valueOf(req.getParameter("name"));
        File file = new File("images");
        String imageName = PsqlStore.instOf().findByIdCandidate(id).getPhotoId();
        for (File f : file.listFiles()) {
            if (f.getName().equals(imageName)) {
                f.delete();
                break;
            }
        }
        PsqlStore.instOf().deleteCandidate(id);
        resp.sendRedirect(req.getContextPath() + "/candidate/candidates.do");
    }
}
