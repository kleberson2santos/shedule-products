PrimeFaces.locales['pt'] = {
    closeText: 'Fechar',
    prevText: 'Anterior',
    nextText: 'Próximo',
    currentText: 'Começo',
    monthNames: ['Janeiro','Fevereiro','Março','Abril','Maio','Junio','Julho','Agosto','Setembro','Outubro','Novembro','Dezembro'],
    monthNamesShort: ['Jan','Fev','Mar','Abr','Mai','Jun', 'Jul','Ago','Set','Out','Nov','Dez'],
    dayNames: ['Domingo','Segunda','Terça','Quarta','Quinta','Sexta','Sábado'],
    dayNamesShort: ['Dom','Seg','Ter','Qua','Qui','Sex','Sáb'],
    dayNamesMin: ['D','S','T','Q','Q','S','S'],
    weekHeader: 'Semana',
    firstDay: 0,
    isRTL: false,
    showMonthAfterYear: false,
    yearSuffix: '',
    timeOnlyTitle: 'Só Horas',
    timeText: 'Tempo',
    hourText: 'Hora',
    minuteText: 'Minuto',
    secondText: 'Segundo',
    ampm: false,
    month: 'Mês',
    week: 'Semana',
    day: 'Dia',
    allDayText : 'Todo o Dia',	
    messages : {
		'org.hibernate.validator.constraints.NotBlank.message' : '{0} não pode estar em branco',
		'javax.validation.constraints.NotNull.message' : '{0} não pode ser nulo'
};

PrimeFaces.validator.NotNull = {

	MESSAGE_ID : "javax.validation.constraints.NotNull.message",

	validate : function(c, d) {
		var label = c.data('p-label');
		if (d === null || d === undefined) {
			var b = PrimeFaces.util.ValidationContext, a = c.data("p-notnull-msg"), e = (a) ? {
				summary : a,
				detail : a
			} : b.getMessage(this.MESSAGE_ID, label);
			throw e
		}
	}
};

PrimeFaces.converter['com.algaworks.Categoria'] = {
	
	convert : function(element, value) {
		if (value === null || value === '') {
			return null;
		}
		
		return parseInt(value);
	}
		
};

PrimeFaces.validator.NotBlank = {
	
	MESSAGE_ID : 'org.hibernate.validator.constraints.NotBlank.message',
		
	validate : function(element, value) {
		if (value === null || value === undefined || value.trim() === '') {
			var msg = element.data('msg-notblank');
			var label = element.data('p-label');
			var context = PrimeFaces.util.ValidationContext;
			var msgObj;
			
			if (!msg) {
				msgObj = context.getMessage(this.MESSAGE_ID, label);
			} else {
				var msgObj = {
					summary : msg,
					detail : msg
				}
			}
			
			throw msgObj;
		}
	}
		
};

PrimeFaces.validator.SKU = {
	
	pattern : /^([a-zA-Z]{2}\d{4,18})?$/,
		
	validate : function(element, value) {
		if (!this.pattern.test(value)) {
			var msg = element.data('msg-sku');
			
			if (!msg) {
				msg = 'SKU não é válido.';
			}
			
			var msgObj = {
				summary : msg,
				detail : msg
			}
			
			throw msgObj;
		}
	}
		
};

PrimeFaces.validator.Highlighter.types.onemenu = {
	highlight : function(a) {
		a.parent().siblings(".ui-selectonemenu-trigger").addClass(
				"ui-state-error").parent().addClass(
				"ui-state-error");
		PrimeFaces.validator.Highlighter.highlightLabel(a.parent().parent().find('div input'))
	},
	unhighlight : function(a) {
		a.parent().siblings(".ui-selectonemenu-trigger")
				.removeClass("ui-state-error").parent()
				.removeClass("ui-state-error");
		PrimeFaces.validator.Highlighter.unhighlightLabel(a
				.parent().parent().find('div input'))
	}
};
