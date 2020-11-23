public class Main {

    public static void main(String[] args) {
        Grammar grammar = new Grammar("g1.txt");
        Parser parser = new Parser(grammar);
        parser.parse();
    }
}
