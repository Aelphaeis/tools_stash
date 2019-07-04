package stash.directives;

import stash.config.Instruction;
import stash.transformer.Transformer;

public interface TransformDirective extends Directive {
	void execute(Instruction i, Transformer t);
	
}
