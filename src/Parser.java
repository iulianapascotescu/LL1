import java.util.*;

public class Parser {
    private Grammar grammar;
    private Map<String, Set<String>> first;
    private Map<String, Set<String>> follow;

    public Parser(Grammar grammar) {
        this.grammar = grammar;
        first = new HashMap<>();
        follow = new HashMap<>();
    }

    public Grammar getGrammar() {
        return grammar;
    }

    public void parse() {
        this.First();
        this.Follow();
        System.out.println("done");
    }

    private void First() {
        this.grammar.getSetOfTerminals().forEach(t -> first.put(t, new HashSet<>(Set.of(t))));
        this.grammar.getSetOfNonterminals().forEach(t -> first.put(t, new HashSet<>()));

        int i = 0;
        this.grammar.getProductions().forEach(p -> {
            if (this.grammar.getSetOfTerminals().contains(p.getSecond().get(0)))
                this.first.get(p.getFirst()).add(p.getSecond().get(0));
        });

        boolean changesMade;
        do {
            i++;
            changesMade = false;
            for (String nonterminal : this.grammar.getSetOfNonterminals()) {
                for (MyPair<String, List<String>> p : this.grammar.getProductionsOfNonterminal(nonterminal)) {
                    Set<String> fminus1 = new HashSet<>(this.first.get(p.getSecond().get(0)));
                    for (String c : p.getSecond()) {
                        if (this.first.get(c).size() == 0) {
                            return;
                        }
                        fminus1.addAll(concatenationWithOne(fminus1, this.first.get(c)));
                    }
                    first.get(nonterminal).addAll(fminus1);
                    if (!this.first.get(nonterminal).containsAll(fminus1))
                        changesMade = true;
                }
            }
        } while (changesMade);
    }


    private Set<String> concatenationWithOne(Set<String> a, Set<String> b) {
        Set<String> result = new HashSet<>(a);
        if (a.contains("epsilon")) {
            result.remove("epsilon");
            result.addAll(b);
        }
        return result;
    }

    private void Follow() {
        this.grammar.getSetOfNonterminals().forEach(t -> follow.put(t, new HashSet<>()));
        this.follow.get(this.grammar.getInitialState()).add("epsilon");

        boolean changesMade;
        do {
            changesMade = false;
            for (String nonterminal : this.grammar.getSetOfNonterminals()) {
                int a = this.follow.get(nonterminal).size();
                for (MyPair<String, List<String>> p : this.grammar.getProductions()) {
                    if (p.getSecond().contains(nonterminal)) {
                        if (p.getSecond().indexOf(nonterminal) + 1 < p.getSecond().size()) {
                            String y = p.getSecond().get(p.getSecond().indexOf(nonterminal) + 1);
                            for (String element : this.first.get(y))
                                if (this.first.get(element).contains("epsilon"))
                                    this.follow.get(nonterminal).addAll(this.follow.get(p.getFirst()));
                                else
                                    this.follow.get(nonterminal).addAll(this.first.get(element));
                        }
                        else
                            this.follow.get(nonterminal).addAll(this.follow.get(p.getFirst()));
                    }
                }
                if (a != this.follow.get(nonterminal).size())
                    changesMade = true;
            }
        } while (changesMade);
    }

}
