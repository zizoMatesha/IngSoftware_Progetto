package srcClass;

public class CodFiscale {
    private String cf;

    public CodFiscale(String cf) {
        this.cf = cf;
        /*if(!codiceFiscale(this.cf)){
            System.err.println("Codice Fiscale non valido!");
        }*/
    }

    private static String minMaj(String min ){

            // Toglie gli spazi vuoti da una String che rappresenta
            // un (presunto) codice fiscale e ne trasforma le minuscole
            // in maiuscole.

            if ( min == null ) return null ;

            String maj = "" ; char ch ;

            for( int i = 0 ; i < min.length() ; i++ )

                if ( ( ch = min.charAt( i ) ) != ' ' &&  ch != '\t' )

                    if ( ch >= 'a' && ch <= 'z' )

                        maj = maj + (char)( ch - 'a' + 'A' ) ;

                    else
                        maj = maj + ch ;

            return maj;
        }

        private static boolean codiceFiscale( String cf ){

            // Implementazione provvisoria di un riconoscitore di codici fiscali:
            // esempi corretti sono: MDOGNE51B25G702P e FRTPLA46M41G224S
            // ma se nel primo di questi due mettessimo '0' al posto di 'O'
            // oppure sostituissimo "GEN" a "GNE", allora diventerebbe scorretto.

            if ( cf == null ) return false ;

            return cf.length() == 16

                    && cognome( cf.substring( 0, 3 ) )

                    && nome( cf.substring( 3, 6 ) )

                    && data_nascita_e_sesso( cf.substring( 6, 11 ) )

                    && comune_o_stato_nascita( cf.substring( 11, 15 ) )

                    && char_ctrl( cf.charAt( 15 ), cf )
                    ;

        }

        private static boolean nome( String nom ){
            return cognome( nom ) ;
        }

        private static boolean cognome( String cog ){

            boolean conson = true ; // mi aspetto una consoante

            for( int i = 0 ; i < cog.length( ); i = i + 1 ){

                char ch = cog.charAt( i ) ;

                if ( conson && ! consonante( ch ) )

                    conson = false; // d'ora in poi mi aspetto una vocale

                if ( ! conson && ! vocale( ch ) ) return false ;
            }

            return true ;
        }

        private static boolean lettera( char ch ){

            return ch >= 'A' && ch <= 'Z';

        }

        private static boolean vocale( char ch ){

            return "AEIOU".indexOf( ch ) != -1 ;
        }

        private static boolean consonante( char ch ){

            return lettera( ch ) && ! vocale( ch ) ;
        }

        private static boolean data_nascita_e_sesso( String data_gen ){

            int gio ; // giorno del mese combinato con il genere ( 'femmina' +40 )

            return cifre( data_gen.substring( 0, 2 ) )

                    && "ABCDEHLMPRST".indexOf( data_gen.charAt( 2 ) ) != -1 // mese

                    && cifre( data_gen.substring( 3 ) )

                    && ((gio = Integer.parseInt( data_gen.substring( 3, 5 ) )) <= 31 && gio > 0

                    ||  gio <= 71 && gio > 40 ) ;
        }

        private static boolean cifre( String cc ){ // riconosce una sequenza di sole cifre

            for( int i = 0 ; i < cc.length( ); i = i + 1 ){

                char ch = cc.charAt( i ) ;

                if ( ch < '0' || ch > '9' ) return false ;
            }

            return true ;
        }

        private static boolean comune_o_stato_nascita( String comune ){ // codice Belfiore

            return lettera( comune.charAt( 0 ) ) && cifre( comune.substring( 1 ) );
        }

        private static boolean char_ctrl( char let , String  codice ){

            // Esercizio: Completare la verifica che il carattere di controllo sia
            // conforme alle regole del codice fiscale.

            return lettera( let ) ;

        }

    public String getCf() {
        return cf;
    }

    public void setCf(String cf) {
        this.cf = cf;
    }

    @Override
    public String toString(){
        return this.cf;
    }
}
