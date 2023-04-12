package fayi.tableObject;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Optional;

@Getter
public class RefSequence {

    public boolean asExpected(Integer pos, String[] strings) {
        Optional<MHSnp> first = snps.stream().filter( mhSnp -> mhSnp.position == pos ).findFirst();
        return first.isPresent() && first.get().match( strings[0].replaceFirst( "-+$","" ), strings[1].replaceFirst( "-+$","" ) );
    }

    @Getter
    public class MHSnp{
        int position;
        String ref;
        String alt;

        public MHSnp(Integer position, String ref, String alt) {
            this.position = position;
            this.ref = ref;
            this.alt = alt;
        }

        public boolean match(String ref, String alt) {
            return ref.equals( this.ref ) && alt.equals( this.alt );
        }
    }
    private final String name;
    private final String chromesome;
    private final int start;
    private final int end;
    private final String sequence;

    private final ArrayList<MHSnp> snps = new ArrayList<>();

    public boolean containsPosition(int pos){
        for(MHSnp mhSnp: snps){
            if(mhSnp.position == pos)
                return true;
        }
        return false;
    }


    public RefSequence(String name, String chromesome, int start, int end, String sequence) {
        this.name = name;
        this.chromesome = chromesome;
        this.start = start;
        this.end = end;
        this.sequence = sequence;
    }


    public void addSnp(Integer position, String ref, String alt){
        snps.add( new MHSnp(position,ref,alt) );
    }
}
