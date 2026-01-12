package ma.oralCare.mvc.ui1;

public interface Navigatable {
    void showView(String viewKey);
    void dispose(); // Pour la déconnexion
}