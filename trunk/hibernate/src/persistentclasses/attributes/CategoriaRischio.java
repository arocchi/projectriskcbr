/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package persistentclasses.attributes;


public class CategoriaRischio {
    String categoria;

    public CategoriaRischio(){
        categoria = new String("No category");
    }

    public CategoriaRischio(String s)
    {
        categoria = s;
    }

    public String getCategoria(){
        return categoria;
    }

    public CategoriaRischio setCategoria(String s){
        categoria = s;
        return this;
    }

    @Override
    public String toString(){
        return categoria;
    }
}
