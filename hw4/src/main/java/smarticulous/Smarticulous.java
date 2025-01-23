package smarticulous;

import smarticulous.db.Exercise;
import smarticulous.db.Submission;
import smarticulous.db.User;
import smarticulous.db.Exercise.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The Smarticulous class, implementing a grading system.
 */
public class Smarticulous {

    /**
     * The connection to the underlying DB.
     * <p>
     * null if the db has not yet been opened.
     */
    Connection db;

    /**
     * Open the {@link Smarticulous} SQLite database.
     * <p>
     * This should open the database, creating a new one if necessary, and set the {@link #db} field
     * to the new connection.
     * <p>
     * The open method should make sure the database contains the following tables, creating them if necessary:
     *
     * <table>
     *   <caption><em>Table name: <strong>User</strong></em></caption>
     *   <tr><th>Column</th><th>Type</th></tr>
     *   <tr><td>UserId</td><td>Integer (Primary Key)</td></tr>
     *   <tr><td>Username</td><td>Text</td></tr>
     *   <tr><td>Firstname</td><td>Text</td></tr>
     *   <tr><td>Lastname</td><td>Text</td></tr>
     *   <tr><td>Password</td><td>Text</td></tr>
     * </table>
     *
     * <p>
     * <table>
     *   <caption><em>Table name: <strong>Exercise</strong></em></caption>
     *   <tr><th>Column</th><th>Type</th></tr>
     *   <tr><td>ExerciseId</td><td>Integer (Primary Key)</td></tr>
     *   <tr><td>Name</td><td>Text</td></tr>
     *   <tr><td>DueDate</td><td>Integer</td></tr>
     * </table>
     *
     * <p>
     * <table>
     *   <caption><em>Table name: <strong>Question</strong></em></caption>
     *   <tr><th>Column</th><th>Type</th></tr>
     *   <tr><td>ExerciseId</td><td>Integer</td></tr>
     *   <tr><td>QuestionId</td><td>Integer</td></tr>
     *   <tr><td>Name</td><td>Text</td></tr>
     *   <tr><td>Desc</td><td>Text</td></tr>
     *   <tr><td>Points</td><td>Integer</td></tr>
     * </table>
     * In this table the combination of ExerciseId and QuestionId together comprise the primary key.
     *
     * <p>
     * <table>
     *   <caption><em>Table name: <strong>Submission</strong></em></caption>
     *   <tr><th>Column</th><th>Type</th></tr>
     *   <tr><td>SubmissionId</td><td>Integer (Primary Key)</td></tr>
     *   <tr><td>UserId</td><td>Integer</td></tr>
     *   <tr><td>ExerciseId</td><td>Integer</td></tr>
     *   <tr><td>SubmissionTime</td><td>Integer</td></tr>
     * </table>
     *
     * <p>
     * <table>
     *   <caption><em>Table name: <strong>QuestionGrade</strong></em></caption>
     *   <tr><th>Column</th><th>Type</th></tr>
     *   <tr><td>SubmissionId</td><td>Integer</td></tr>
     *   <tr><td>QuestionId</td><td>Integer</td></tr>
     *   <tr><td>Grade</td><td>Real</td></tr>
     * </table>
     * In this table the combination of SubmissionId and QuestionId together comprise the primary key.
     *
     * @param dburl The JDBC url of the database to open (will be of the form "jdbc:sqlite:...")
     * @return the new connection
     * @throws SQLException
     */
    public Connection openDB(String dburl) throws SQLException {
        Connection db = DriverManager.getConnection(dburl);
        Statement st = db.createStatement();
        st.executeUpdate("CREATE TABLE IF NOT EXISTS User (" +
            "UserId INTEGER PRIMARY KEY, "+
            "Username TEXT UNIQUE, "+
            "Firstname TEXT, "+
            "Lastname TEXT, "+
            "Password TEXT" +
        ");");

        st.executeUpdate("CREATE TABLE IF NOT EXISTS Exercise (" +
            "ExerciseId INTEGER PRIMARY KEY, "+
            "Name TEXT, "+
            "DueDate INTEGER"+
        ");");

        st.executeUpdate("CREATE TABLE IF NOT EXISTS Question (" +
            "ExerciseId INTEGER, "+
            "QuestionId INTEGER, "+
            "Name TEXT, "+
            "Desc TEXT, "+
            "Points INTEGER, "+
            "PRIMARY KEY (ExerciseId, QuestionId), " +
            "FOREIGN KEY (ExerciseId) REFERENCES Exercise(ExerciseId)" +
        ");");

        st.executeUpdate("CREATE TABLE IF NOT EXISTS Submission ("+
            "SubmissionId INTEGER PRIMARY KEY," +
            "UserId INTEGER, "+
            "ExerciseId INTEGER, "+
            "SubmissionTime INTEGER, "+
            "FOREIGN KEY (UserId) REFERENCES User(UserId), " +
            "FOREIGN KEY (ExerciseId) REFERENCES Exercise(ExerciseId)" +
        ");");

        st.executeUpdate("CREATE TABLE IF NOT EXISTS QuestionGrade ("+
            "SubmissionId INTEGER," +
            "QuestionId INTEGER, "+
            "Grade REAL, "+
            "PRIMARY KEY (SubmissionId, QuestionId),"+
            "FOREIGN KEY (SubmissionId) REFERENCES Submission(SubmissionId),"+
            "FOREIGN KEY (QuestionId) REFERENCES Question(QuestionId)"+
        ");");
        this.db = db;
        return db;
    }


    /**
     * Close the DB if it is open.
     *
     * @throws SQLException
     */
    public void closeDB() throws SQLException {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    // =========== User Management =============

    /**
     * Add a user to the database / modify an existing user.
     * <p>
     * Add the user to the database if they don't exist. If a user with user.username does exist,
     * update their password and firstname/lastname in the database.
     *
     * @param user
     * @param password
     * @return the userid.
     * @throws SQLException
     */
    public int addOrUpdateUser(User user, String password) throws SQLException {
        int userId = -1; //initialize the userId
        String checkUserQuery =  "SELECT UserId FROM User WHERE Username = ?"; //build a pattern to the query that verify if the user already exist
        PreparedStatement selectUserIdStatement = db.prepareStatement(checkUserQuery);//build the PreparedStatement named selectUserIdStatement that implement the selectUserIdQuery query
        selectUserIdStatement.setString(1, user.username); //set the first ? to the user name value
        ResultSet rs = selectUserIdStatement.executeQuery();//execute the query with the PreparedStatement on the selectUserIdQuery query and store the result in rs
        if(rs.next()){ //if the user already exist
            userId = rs.getInt("UserId"); //get the user id from the rs
            String updateQuery = "UPDATE User SET Password = ?, Firstname = ?, Lastname = ? WHERE UserId = " + userId; //update all the ? to the userId value that we know that exist according to the arguments that was given by the method and the userId that we already know
            PreparedStatement updateStmt = db.prepareStatement(updateQuery);
            updateStmt.setString(1, password);
            updateStmt.setString(2, user.firstname);
            updateStmt.setString(3, user.lastname);
            updateStmt.executeUpdate(); // execute the update
            updateStmt.close(); //close the update statement (resource)
        }else{ //if the user don't exist
            String insertQuery = "INSERT INTO User (Username, Password, Firstname, Lastname) VALUES (?, ?, ?, ?)";//build a pattern to the query insert new user
            PreparedStatement insertStmt = db.prepareStatement(insertQuery); //set all the ? to the user values that we create according to the arguments that was given by the method
            insertStmt.setString(1, user.username);
            insertStmt.setString(2, password);
            insertStmt.setString(3, user.firstname);
            insertStmt.setString(4, user.lastname);
            insertStmt.executeUpdate();// execute the insert
            insertStmt.close();//close the insert statement (resource)
        
            rs = selectUserIdStatement.executeQuery();//execute the query with the PreparedStatement on the selectUserIdQuery query and store the result in rs again (after the insert)
            rs.next();
            userId = rs.getInt("UserId");//get the user id from the rs
        }
        rs.close();//close the ResultSet (resource)
        selectUserIdStatement.close();//close the prepareStatement (resource)
        return userId;
    }


    /**
     * Verify a user's login credentials.
     *
     * @param username
     * @param password
     * @return true if the user exists in the database and the password matches; false otherwise.
     * @throws SQLException
     * <p>
     * Note: this is totally insecure. For real-life password checking, it's important to store only
     * a password hash
     * @see <a href="https://crackstation.net/hashing-security.htm">How to Hash Passwords Properly</a>
     */
    public boolean verifyLogin(String username, String password) throws SQLException {
        String checkUserPasswordQuery = "SELECT Password FROM User WHERE Username = ?"; //build a pattern to the query that pull out the password that fit the user name that was given by the method
        PreparedStatement checkUserPasswordStmt = db.prepareStatement(checkUserPasswordQuery);//build the PreparedStatement named checkUserPasswordStmt that implement the checkUserPasswordQuery query
        checkUserPasswordStmt.setString(1, username);//set the first ? to the user name value
        ResultSet rs = checkUserPasswordStmt.executeQuery();//execute the query with the PreparedStatement on the checkUserPasswordStmt query and store the result in rs
        if(rs.next()){//if the user exist
            String storedPassword = rs.getString("Password"); //take the password from rs
            rs.close(); //close the ResultSet (resource)
            checkUserPasswordStmt.close(); //close the prepareStatement (resource)
            return password.equals(storedPassword); //return true or false if the password that was given by the method is equal to the real password of the user
        }else //if the user doesn't exist
        rs.close(); //close the ResultSet (resource)
        checkUserPasswordStmt.close(); //close the prepareStatement (resource)
        return false; //automatically return false
    }

    // =========== Exercise Management =============

    /**
     * Add an exercise to the database.
     *
     * @param exercise
     * @return the new exercise id, or -1 if an exercise with this id already existed in the database.
     * @throws SQLException
     */
    public int addExercise(Exercise exercise) throws SQLException {
        int newExId = -1;
        String checkExIdQuery = "SELECT ExerciseId FROM EXERCISE WHERE ExerciseId = ?"; //build a pattern to the query that pull out the ExerciseId that fit the ExerciseId that given by exercise
        PreparedStatement checkExIdStmt = db.prepareStatement(checkExIdQuery);//build the PreparedStatement named checkExIdStmt that implement the checkExIdQuery query
        checkExIdStmt.setInt(1, exercise.id);//set the first ? to the exercise.id value
        ResultSet rs = checkExIdStmt.executeQuery();//execute the query with the PreparedStatement on the checkExIdStmt query and store the result in rs
        if(!rs.next()){//if there is not a exercise.id like the one that was given as argument
            String insertQuery = "INSERT INTO Exercise (ExerciseId, Name, DueDate) VALUES (?, ?, ?)"; //build a pattern to the query that pull out from the Exercise the data about the new column
            PreparedStatement insertStmt = db.prepareStatement(insertQuery);//build the PreparedStatement named insertStmt that implement the insertQuery query
            insertStmt.setInt(1, exercise.id);//set all the ? to the exercise values
            insertStmt.setString(2, exercise.name);
            insertStmt.setLong(3, exercise.dueDate.getTime());
            insertStmt.executeUpdate();
            insertStmt.close(); //close the prepareStatement (resource)
            newExId = exercise.id; //save the exercise.id as the new newExId
            int questionId = 1; //alocate counter to the questionId that don't given as argument (according to check in the test method)
            if(exercise.questions != null){ //if the exercise.questions field does not not (means there are questions that related to this exercise)
                String insertQuestionQuery = "INSERT INTO Question (ExerciseId, QuestionId, Name, Desc, Points) VALUES (?,?,?,?,?)";//build a pattern to the query that pull out the Question parameters that fit the questions that given by exercise
                PreparedStatement insertQuestionStmt = db.prepareStatement(insertQuestionQuery);//build the PreparedStatement named insertQuestionStmt that implement the insertQuestionQuery query
                for(Question question : exercise.questions){ //loop that iterate all the questions that in the specific exercise
                    insertQuestionStmt.setInt(1, exercise.id);//set all the ? to the question values
                    insertQuestionStmt.setInt(2, questionId);
                    insertQuestionStmt.setString(3, question.name);
                    insertQuestionStmt.setString(4, question.desc);
                    insertQuestionStmt.setInt(5, question.points);
                    insertQuestionStmt.executeUpdate();
                    questionId++; //add to the counter to the questionId column to define each question
                }
                insertQuestionStmt.close(); //close the prepareStatement (resource)
            }
        }
        rs.close();
        checkExIdStmt.close(); //close the prepareStatement (resource)
        return newExId;
    }


    /**
     * Return a list of all the exercises in the database.
     * <p>
     * The list should be sorted by exercise id.
     *
     * @return list of all exercises.
     * @throws SQLException
     */
    public List<Exercise> loadExercises() throws SQLException {
        List<Exercise> exercises = new ArrayList<>(); //allocate new ex list
        Statement st = db.createStatement(); //make a new statement
        ResultSet rs = st.executeQuery("SELECT * FROM Exercise ORDER BY ExerciseId"); //build query named rs
        while (rs.next()){//iterate all the Exercises (order by ExerciseId)
            int ExerciseId = rs.getInt("ExerciseId");//set all the fields of each ex
            String name = rs.getString("Name");
            long dueDate = rs.getLong("DueDate");
            Exercise exercise = new Exercise(ExerciseId, name, new Date(dueDate)); //make new object for each line in the exercise with the appropriate data

            Statement stQuestion = db.createStatement();//make a new statement
            ResultSet rsQuestion = stQuestion.executeQuery ("SELECT * FROM Question WHERE ExerciseId = "+ ExerciseId);//for each ex we want the related questions
            while(rsQuestion.next()){//while there is more questions
                String questionName = rsQuestion.getString("Name");//set all the fields of each question
                String questionDesc = rsQuestion.getString("Desc");
                int questionPoints = rsQuestion.getInt("Points");
                exercise.questions.add(exercise.new Question(questionName, questionDesc, questionPoints));//make a new obj for each question and add to the list of questions for each ex
            }
            exercises.add(exercise); //add the ex to the list of ex
            rsQuestion.close(); //close the Statement (resource)
        }
        rs.close();//close the Statement (resource)
        st.close();//close the Statement (resource)
        return exercises;
    }

    // ========== Submission Storage ===============

    /**
     * Store a submission in the database.
     * The id field of the submission will be ignored if it is -1.
     * <p>
     * Return -1 if the corresponding user doesn't exist in the database.
     *
     * @param submission
     * @return the submission id.
     * @throws SQLException
     */
    public int storeSubmission(Submission submission) throws SQLException {
        int userId = getUserIdByUsername(submission.user.username);  //find the userId based on Username using getUserIdByUsername method that I build and save userId in userId int
        if(userId == -1){ //if user name does not exist return -1
            return -1;
        }

        if(submission.id == -1){ //if this is a new submission
            String insertSubmissionQuery = "INSERT INTO Submission (UserId, ExerciseId, SubmissionTime) VALUES (?,?,?)"; //prepare sql query that insert new row to submission
            PreparedStatement insertSubmissionStmt = db.prepareStatement(insertSubmissionQuery, Statement.RETURN_GENERATED_KEYS); //make an prepared statement to make insertSubmissionQuery query and let us get the primary key that create automatically (submissionId) based on URL: "https://www.geeksforgeeks.org/how-to-get-the-insert-id-in-jdbc/"
            insertSubmissionStmt.setInt(1, userId); // set all the ? parameters with the appropriate data
            insertSubmissionStmt.setInt(2, submission.exercise.id);
            insertSubmissionStmt.setLong(3, submission.submissionTime.getTime());
            insertSubmissionStmt.executeUpdate(); //execute the query and add the statement to the submission chart
            ResultSet rs = insertSubmissionStmt.getGeneratedKeys(); //using the prepareStatement to get the new primary key that we create
            if(rs.next()){ //if we really create one
                submission.id = rs.getInt(1); //take the result that we get in the SubmissionId and update the submission.id field of the object
            }
            rs.close(); //close resource
            insertSubmissionStmt.close(); //close resource
        }
        return submission.id;

    }


    public int getUserIdByUsername(String username) throws SQLException { //a method that take an username object and return the userId of him.
        String CheckUserIdQuery = "SELECT UserId FROM User WHERE Username = ?"; //make a pattern to the query that check if there exist User name as we look for
        PreparedStatement CheckUserIdStmt = db.prepareStatement(CheckUserIdQuery); //make a PreparedStatement to the query that check if there exist User name as we look for
        CheckUserIdStmt.setString(1, username); //set the ? with the username that was given by the submission object
        ResultSet rsCheckUserId = CheckUserIdStmt.executeQuery(); //save the result of the query in rsCheckUserId

        if(rsCheckUserId.next()){ //if there is such user
            int userId = rsCheckUserId.getInt("UserId"); //userId will get the value of UserId from the column
            rsCheckUserId.close();//close resource
            CheckUserIdStmt.close();//close resource
            return userId;//return the UserId that we found
        }
        rsCheckUserId.close(); //close resource
        CheckUserIdStmt.close(); //close resource
        return -1; //if not found return -1
    }



    // ============= Submission Query ===============


    /**
     * Return a prepared SQL statement that, when executed, will
     * return one row for every question of the latest submission for the given exercise by the given user.
     * <p>
     * The rows should be sorted by QuestionId, and each row should contain:
     * - A column named "SubmissionId" with the submission id.
     * - A column named "QuestionId" with the question id,
     * - A column named "Grade" with the grade for that question.
     * - A column named "SubmissionTime" with the time of submission.
     * <p>
     * Parameter 1 of the prepared statement will be set to the User's username, Parameter 2 to the Exercise Id, and
     * Parameter 3 to the number of questions in the given exercise.
     * <p>
     * This will be used by {@link #getLastSubmission(User, Exercise)}
     *
     * @return
     */
    PreparedStatement getLastSubmissionGradesStatement() throws SQLException {
        String LastSubmissionGradesStatementQuery =
            "SELECT "+
            "    Submission.SubmissionId, "+
            "    QuestionGrade.QuestionId, "+
            "    QuestionGrade.Grade, "+
            "    Submission.SubmissionTime "+
            "FROM "+
            "    Submission "+
            "JOIN "+
            "    QuestionGrade ON Submission.SubmissionId = QuestionGrade.SubmissionId "+
            "WHERE "+
            "    Submission.UserId = (SELECT UserId FROM User WHERE Username = ?) AND ExerciseId = ? "+
            "ORDER BY "+
            "    Submission.SubmissionTime DESC ,"+
            "    QuestionGrade.QuestionId ASC "+
            "LIMIT ?";
        return db.prepareStatement(LastSubmissionGradesStatementQuery);
    }

    /**
     * Return a prepared SQL statement that, when executed, will
     * return one row for every question of the <i>best</i> submission for the given exercise by the given user.
     * The best submission is the one whose point total is maximal.
     * <p>
     * The rows should be sorted by QuestionId, and each row should contain:
     * - A column named "SubmissionId" with the submission id.
     * - A column named "QuestionId" with the question id,
     * - A column named "Grade" with the grade for that question.
     * - A column named "SubmissionTime" with the time of submission.
     * <p>
     * Parameter 1 of the prepared statement will be set to the User's username, Parameter 2 to the Exercise Id, and
     * Parameter 3 to the number of questions in the given exercise.
     * <p>
     * This will be used by {@link #getBestSubmission(User, Exercise)}
     *
     */
    PreparedStatement getBestSubmissionGradesStatement() throws SQLException {
        // TODO: Implement
        return null;
    }

    /**
     * Return a submission for the given exercise by the given user that satisfies
     * some condition (as defined by an SQL prepared statement).
     * <p>
     * The prepared statement should accept the user name as parameter 1, the exercise id as parameter 2 and a limit on the
     * number of rows returned as parameter 3, and return a row for each question corresponding to the submission, sorted by questionId.
     * <p>
     * Return null if the user has not submitted the exercise (or is not in the database).
     *
     * @param user
     * @param exercise
     * @param stmt
     * @return
     * @throws SQLException
     */
    Submission getSubmission(User user, Exercise exercise, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, user.username);
        stmt.setInt(2, exercise.id);
        stmt.setInt(3, exercise.questions.size());

        ResultSet res = stmt.executeQuery();

        boolean hasNext = res.next();
        if (!hasNext)
            return null;

        int sid = res.getInt("SubmissionId");
        Date submissionTime = new Date(res.getLong("SubmissionTime"));

        float[] grades = new float[exercise.questions.size()];

        for (int i = 0; hasNext; ++i, hasNext = res.next()) {
            grades[i] = res.getFloat("Grade");
        }

        return new Submission(sid, user, exercise, submissionTime, (float[]) grades);
    }

    /**
     * Return the latest submission for the given exercise by the given user.
     * <p>
     * Return null if the user has not submitted the exercise (or is not in the database).
     *
     * @param user
     * @param exercise
     * @return
     * @throws SQLException
     */
    public Submission getLastSubmission(User user, Exercise exercise) throws SQLException {
        return getSubmission(user, exercise, getLastSubmissionGradesStatement());
    }


    /**
     * Return the submission with the highest total grade
     *
     * @param user the user for which we retrieve the best submission
     * @param exercise the exercise for which we retrieve the best submission
     * @return
     * @throws SQLException
     */
    public Submission getBestSubmission(User user, Exercise exercise) throws SQLException {
        return getSubmission(user, exercise, getBestSubmissionGradesStatement());
    }
}
