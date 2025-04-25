package com.programacao.web.webstore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Carrinho {

        private List<ItemCarrinho> itens = new ArrayList<>();

        public void addProduto(Produto produto) {
            boolean encontrado = false;

            for (ItemCarrinho item : itens) {
                if (item.getProduto().getId() == produto.getId()) {
                    item.incrementarQuantidade();
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                itens.add(new ItemCarrinho(produto, 1));
            }
        }

         public void removeProduto(int id) {

            for (Iterator<ItemCarrinho> iterator = itens.iterator(); iterator.hasNext();) {
                ItemCarrinho item = iterator.next();

                if (item.getProduto().getId() == id) {
                    item.decrementarQuantidade();

                    if (item.getQuantidade() <= 0) {
                        iterator.remove();
                    }

                    break;
                }
            }
         }


    public List<ItemCarrinho> getItens() {
            return itens;
        }


}



