package controller;

import java.sql.Date;

public class Evento {
    private int idEvento;
    private String nome;
    private Date dataEvento;
    private String local;
    private String descricao;
    private float valorIngresso;
    private int quantidadeIngressos;

    public int getIdEvento() { return idEvento; }
    public void setIdEvento(int idEvento) { this.idEvento = idEvento; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Date getDataEvento() { return dataEvento; }
    public void setDataEvento(Date dataEvento) { this.dataEvento = dataEvento; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public float getValorIngresso() { return valorIngresso; }
    public void setValorIngresso(float valorIngresso) { this.valorIngresso = valorIngresso; }

    public int getQuantidadeIngressos() { return quantidadeIngressos; }
    public void setQuantidadeIngressos(int quantidadeIngressos) { this.quantidadeIngressos = quantidadeIngressos; }
}
