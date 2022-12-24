import view.MainWindow;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main (String [] args) {
        try {
            new MainWindow();
        } catch (IOException ex) {} catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
