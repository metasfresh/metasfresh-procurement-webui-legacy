package de.metas.procurement.webui.ui.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.i18n.I18N;

import com.vaadin.addon.touchkit.ui.NavigationButton;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickEvent;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickListener;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.ui.VerticalLayout;

import de.metas.procurement.webui.MFProcurementUI;
import de.metas.procurement.webui.MFSession;
import de.metas.procurement.webui.model.Rfq;
import de.metas.procurement.webui.model.User;
import de.metas.procurement.webui.service.IRfQService;

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
public class RfQsListView extends MFProcurementNavigationView
{
	@Autowired
	private IRfQService rfqService;

	@Autowired
	private I18N i18n;

	private final User user;

	public RfQsListView()
	{
		super();
		// Application.autowire(this);

		final MFSession mfSession = MFProcurementUI.getCurrentMFSession();
		user = mfSession.getUser();

		setCaption(i18n.get("RfQsListView.caption"));
	}

	@Override
	public void attach()
	{
		super.attach();

		final VerticalLayout content = new VerticalLayout();
		setContent(content);

		final VerticalComponentGroup rfqsPanel = new VerticalComponentGroup();
		for (final Rfq rfq : rfqService.getActiveRfQs(user))
		{
			final NavigationButton rfqButton = createRfQButton(rfq);
			rfqsPanel.addComponent(rfqButton);
		}
		content.addComponent(rfqsPanel);
	}

	private NavigationButton createRfQButton(final Rfq rfq)
	{
		final NavigationButton button = new NavigationButton();
		button.setTargetView(this);
		button.setCaption(rfq.getName());

		// TODO: FRESH-402: Description, etc

		button.addClickListener(new NavigationButtonClickListener()
		{
			@Override
			public void buttonClick(final NavigationButtonClickEvent event)
			{
				onRfQSelected(rfq);
			}
		});

		return button;
	}

	private void onRfQSelected(final Rfq rfq)
	{
		final RfQView rfqView = new RfQView(rfq);
		getNavigationManager().navigateTo(rfqView);
	}

}
