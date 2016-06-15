package de.metas.procurement.webui.ui.view;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.i18n.I18N;

import com.vaadin.addon.touchkit.ui.NumberField;
import com.vaadin.data.util.converter.StringToBigDecimalConverter;
import com.vaadin.data.validator.BigDecimalRangeValidator;
import com.vaadin.ui.CssLayout;

import de.metas.procurement.webui.model.RfqQty;
import de.metas.procurement.webui.util.JavascriptUtils;
import de.metas.procurement.webui.util.QuantityUtils;

/*
 * #%L
 * metasfresh-procurement-webui
 * %%
 * Copyright (C) 2016 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

@SuppressWarnings("serial")
public class RfQQuantityView extends MFProcurementNavigationView
{
	private static final String MSG_InvalidQty = "DailyProductQtyReportView.error.InvalidValue"; // reuse
	
	@Autowired
	private I18N i18n;
	
	private final RfqQty rfqQty;
	
	private NumberField qty;

	public RfQQuantityView(final RfqQty rfqQty)
	{
		super();
		// Application.autowire(this);

		this.rfqQty = rfqQty;
		
		setCaption(i18n.get("RfQQuantityView.caption"));
	}
	
	@Override
	public void attach()
	{
		super.attach();

		final CssLayout content = new CssLayout();
		setContent(content);

		qty = new NumberField();
		qty.setConverter(new StringToBigDecimalConverter(){
			@Override
			protected NumberFormat getFormat(Locale locale)
			{
				final NumberFormat format = super.getFormat(locale);
				format.setGroupingUsed(false); // FRESH-126
				return format;
			}
		});
		qty.addValidator(new BigDecimalRangeValidator(i18n.get(MSG_InvalidQty), BigDecimal.ZERO, null)); // FRESH-144
		qty.setImmediate(true);

		// NOTE: because "qty.selectAll" seems to not work, we are doing this job directly in javascript.
		qty.setId(RfQQuantityView.class.getSimpleName() + "_qty");
		JavascriptUtils.enableSelectAllOnFocus(qty);
		
		qty.setValue(QuantityUtils.toString(rfqQty.getQtyPromised()));
		
		content.addComponent(qty);
		
		// TODO FRESH-402: add listener, implement functionality etc
	}
}
