package cs.uwf.edu.ktane.bomb;

import lombok.Getter;
import lombok.Setter;

public abstract class ModuleBase implements Module {

    @Getter
    @Setter
    protected Bomb bomb;
}
