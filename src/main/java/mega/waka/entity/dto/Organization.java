package mega.waka.entity.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Organization {

    Dotgabi("돗가비"),
    Megabrain("메가브레인");

    @Getter
    private final String name;
}
