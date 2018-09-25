package net.thisptr.jackson.jq.internal.functions;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;

import net.thisptr.jackson.jq.Expression;
import net.thisptr.jackson.jq.Function;
import net.thisptr.jackson.jq.Output;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.Version;
import net.thisptr.jackson.jq.exception.JsonQueryException;
import net.thisptr.jackson.jq.exception.JsonQueryTypeException;
import net.thisptr.jackson.jq.internal.BuiltinFunction;

@BuiltinFunction("fromjson/0")
public class FromJsonFunction implements Function {
	@Override
	public void apply(final Scope scope, final List<Expression> args, final JsonNode in, final Output output, final Version version) throws JsonQueryException {
		if (!in.isTextual())
			throw new JsonQueryTypeException("%s only strings can be parsed", in);

		final JsonNode tree;
		try (final JsonParser parser = scope.getObjectMapper().getFactory().createParser(in.asText())) {
			tree = parser.readValueAsTree();
			if (tree == null)
				throw JsonQueryException.format("failed to parse %s as json; empty", in);
			if (parser.nextToken() != null)
				throw JsonQueryException.format("failed to parse %s as json; trailing data", in);
		} catch (final JsonQueryException e) {
			throw e;
		} catch (final IOException e) {
			throw JsonQueryException.format("failed to parse %s as json", in);
		}
		output.emit(tree);
	}
}
