package hummel.service.impl;

import hummel.bean.Author;
import hummel.bean.Book;
import hummel.bean.container.Page;
import hummel.dao.AuthorDao;
import hummel.dao.BookDao;
import hummel.dao.UserDao;
import hummel.dao.ex.AuthorDaoEx;
import hummel.dao.ex.BookDaoEx;
import hummel.dao.ex.UserDaoEx;
import hummel.exception.ConnectionException;
import hummel.exception.DatabaseException;
import hummel.exception.ServiceException;
import hummel.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

import static hummel.utils.Constants.*;

@Service
public class AdminServiceImpl implements AdminService {
	@Autowired
	private AuthorDao authorDao;
	@Autowired
	private BookDao bookDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private AuthorDaoEx authorDaoEx;
	@Autowired
	private BookDaoEx bookDaoEx;
	@Autowired
	private UserDaoEx userDaoEx;

	@Override
	public void addAuthor(HttpServletRequest request, HttpServletResponse response) throws ServiceException, DatabaseException {
		try {
			var name = request.getParameter(AUTHOR_NAME);
			var filePart = request.getPart(AUTHOR_FILE);
			var fileName = filePart.getSubmittedFileName();
			fileName = name + "." + fileName.split("\\.")[1];
			var fileContent = filePart.getInputStream();
			Files.copy(fileContent, new File("D:\\Source\\Web-Technologies-II\\Lab03\\src\\main\\webapp\\assets\\authors\\" + fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
			authorDao.ex(authorDaoEx).addAuthor(Author.builder().name(name).imagePath("assets/authors/" + fileName).build());
			response.sendRedirect(request.getContextPath() + "/admin");
		} catch (ServletException | IOException e) {
			throw new ServiceException(SERVICE_EXCEPTION);
		} catch (SQLException | ConnectionException e) {
			throw new DatabaseException(DB_EXCEPTION);
		}
	}

	@Override
	public void addBook(HttpServletRequest request, HttpServletResponse response) throws ServiceException, DatabaseException {
		try {
			var name = request.getParameter(BOOK_NAME);
			var description = request.getParameter(BOOK_DESCRIPTION);
			var filePart = request.getPart(BOOK_FILE);
			var author = request.getParameter(BOOK_AUTHOR);
			var price = Double.parseDouble(request.getParameter(BOOK_PRICE));
			var fileName = filePart.getSubmittedFileName();
			fileName = name + "." + fileName.split("\\.")[1];
			var fileContent = filePart.getInputStream();
			Files.copy(fileContent, new File("D:\\Source\\Web-Technologies-II\\Lab03\\src\\main\\webapp\\assets\\books\\" + fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
			bookDao.ex(bookDaoEx).addBook(Book.builder().name(name).description(description).imagePath("assets/books/" + fileName).author(author).price(price).build());
			response.sendRedirect(request.getContextPath() + "/admin");
		} catch (ServletException | IOException e) {
			throw new ServiceException(SERVICE_EXCEPTION);
		}
	}

	@Override
	public void banUser(HttpServletRequest request, HttpServletResponse response) throws ServiceException, DatabaseException {
		try {
			var banEmail = request.getParameter(BAN_EMAIL);
			var user = userDao.ex(userDaoEx).getUserByEmail(banEmail);
			if (user != null) {
				userDao.ex(userDaoEx).updateBanStatus(1, user.getId());
				user.setBanned(1);
			}
			response.sendRedirect(request.getContextPath() + "/admin");
		} catch (IOException e) {
			throw new ServiceException(SERVICE_EXCEPTION);
		} catch (ConnectionException | SQLException e) {
			throw new DatabaseException(DB_EXCEPTION);
		}
	}

	@Override
	public void getAdminPage(ServletRequest request, ServletResponse response) throws ServiceException, DatabaseException {
		var requestDispatcher = request.getServletContext().getRequestDispatcher(PREFIX + ADMIN_PAGE + POSTFIX);
		try {
			request.setAttribute(AUTHORS, authorDao.ex(authorDaoEx).getAuthors(new Page(0, 999)));
			requestDispatcher.forward(request, response);
		} catch (IOException | ServletException e) {
			throw new ServiceException(SERVICE_EXCEPTION);
		} catch (ConnectionException | SQLException e) {
			throw new DatabaseException(DB_EXCEPTION);
		}
	}
}