package com.example.demo.searcher;

//文档ID和文档的相关性 权重进行包裹
public class Weight {

    private int docId;


    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    //这个weight就表示文档和词的相关性
    //这个值越大,就认为相关性越强
    private int weight;
}
