/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package persistentclasses.attributes;


public class CategoriaRischio {
    String categoria;

    public CategoriaRischio(){}

    public CategoriaRischio(String s)
    {
        categoria = s;
    }

    public String getCategoria(){
        return categoria;
    }

    public CategoriaRischio setCategoria(String s){
        categoria = new String(s);
        return this;
    }

    @Override
    public String toString(){
        return categoria;
    }
}
