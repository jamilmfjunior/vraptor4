package br.com.caelum.vraptor.validator;

import static com.google.common.base.MoreObjects.firstNonNull;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import br.com.caelum.vraptor.Result;

@RequestScoped
public class MessagesProducer {

	private static final String MESSAGES_KEY = "vmessages";
	
	private final Result result;

	/**
	 * @deprecated CDI eyes only
	 */
	protected MessagesProducer() {
		this(null);
	}

	@Inject
	public MessagesProducer(Result result) {
		this.result = result;
	}

	@Produces @RequestScoped
	public Messages create() {
		Messages messages = (Messages) result.included().get(MESSAGES_KEY);
		messages = firstNonNull(messages, new Messages());
		result.include(MESSAGES_KEY, messages);
		return messages;
	}

}
