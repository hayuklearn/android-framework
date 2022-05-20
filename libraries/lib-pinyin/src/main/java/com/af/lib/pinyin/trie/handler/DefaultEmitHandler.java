package com.af.lib.pinyin.trie.handler;

import com.af.lib.pinyin.trie.Emit;

import java.util.ArrayList;
import java.util.List;

public class DefaultEmitHandler implements EmitHandler {

    private List<Emit> emits = new ArrayList<>();

    @Override
    public void emit(Emit emit) {
        this.emits.add(emit);
    }

    public List<Emit> getEmits() {
        return this.emits;
    }

}

