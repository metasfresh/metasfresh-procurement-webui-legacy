package de.metas.procurement.webui.ui.view;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.i18n.I18N;

import com.google.gwt.thirdparty.guava.common.base.Preconditions;
import com.vaadin.addon.touchkit.ui.NavigationButton;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickEvent;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickListener;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.metas.procurement.webui.model.Rfq;
import de.metas.procurement.webui.model.RfqQty;
import de.metas.procurement.webui.service.IRfQService;
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

@SuppressWarnings("serial")
public class RfQView extends MFProcurementNavigationView
{
	@Autowired
	private IRfQService rfqService;

	@Autowired
	private I18N i18n;

	private final Rfq rfq;
	private final Map<Date, RfqQty> day2qty;

	private final DateFormat dateFormat;

	public RfQView(final Rfq rfq)
	{
		super();
		// Application.autowire(this);

		this.rfq = Preconditions.checkNotNull(rfq, "rfq is null");
		day2qty = rfqService.getRfQQuantitiesIndexedByDay(rfq);

		final Locale locale = UI.getCurrent().getLocale();
		dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);

		setCaption(i18n.get("RfQView.caption"));
	}

	@Override
	public void attach()
	{
		super.attach();

		final VerticalLayout content = new VerticalLayout();
		setContent(content);

		final VerticalComponentGroup qtysPanel = new VerticalComponentGroup();
		final Set<Date> days = new TreeSet<>(day2qty.keySet());
		for (final Date day : days)
		{
			final RfqQty qty = day2qty.get(day);
			final NavigationButton button = createButton(qty);
			qtysPanel.addComponent(button);
		}
		content.addComponent(qtysPanel);
	}

	private NavigationButton createButton(final RfqQty rfqQty)
	{
		final NavigationButton button = new NavigationButton();
		button.setTargetView(this);

		final Date day = rfqQty.getDatePromised();
		final String dayStr = dateFormat.format(day);
		button.setCaption(dayStr);

		final BigDecimal qty = rfqQty.getQtyPromised();
		final String qtyStr = QuantityUtils.toString(qty);
		button.setDescription(qtyStr);

		// TODO: FRESH-402: more info, pimp it up!

		button.addClickListener(new NavigationButtonClickListener()
		{
			@Override
			public void buttonClick(final NavigationButtonClickEvent event)
			{
				onRfQQuantitySelected(rfqQty);
			}
		});

		return button;
	}

	protected void onRfQQuantitySelected(final RfqQty rfqQty)
	{
		final RfQQuantityView qtyView = new RfQQuantityView(rfqQty);
		getNavigationManager().navigateTo(qtyView);
	}
}
