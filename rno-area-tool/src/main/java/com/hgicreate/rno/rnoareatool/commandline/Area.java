package com.hgicreate.rno.rnoareatool.commandline;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Area {

    private int id;
    private int code;
    private String name;
    private int parent_id;
    private String first_letter;
    private int level;

}
