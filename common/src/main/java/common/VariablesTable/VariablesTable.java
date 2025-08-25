package common.VariablesTable;

import common.Symbol.Symbol;
import common.responses.CorrectResult;
import common.responses.IncorrectResult;
import common.responses.Result;

import java.util.HashMap;

public record VariablesTable(HashMap<String, Symbol> table) implements VariablesTableInterface{
    @Override
    public Result getTable() {
        return new CorrectResult<>(this.table);
    }

    @Override
    public Result getValue(String name) {
        return new CorrectResult<>(this.table.get(name));
    }

    @Override
    public Result addValue(String name, Symbol value) {
        return new CorrectResult<>( new HashMap<String,Symbol>().put(name,value));
    }

    @Override
    public Result removeValue(String name) {
        return new CorrectResult<>(new HashMap<String,Symbol>(table).remove(name));
    }

    @Override
    public Result exist(String name) {
        if(!table.containsKey(name)){
            return new IncorrectResult("Value not exists!");
        }
        return new CorrectResult<>(null);
    }
}
