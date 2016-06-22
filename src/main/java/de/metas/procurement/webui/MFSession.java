package de.metas.procurement.webui;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.google.gwt.thirdparty.guava.common.base.Preconditions;
import com.vaadin.data.util.BeanItemContainer;

import de.metas.procurement.webui.model.BPartner;
import de.metas.procurement.webui.model.Contracts;
import de.metas.procurement.webui.model.User;
import de.metas.procurement.webui.service.IContractsService;
import de.metas.procurement.webui.service.IRfQService;
import de.metas.procurement.webui.service.ISendService;
import de.metas.procurement.webui.service.impl.SendService;
import de.metas.procurement.webui.ui.model.ProductQtyReportRepository;
import de.metas.procurement.webui.ui.model.RfqHeader;

/*
 * #%L
 * de.metas.procurement.webui
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

public final class MFSession
{
	public static final Builder builder()
	{
		return new Builder();
	}

	private final User user;
	private final Contracts contracts;
	private final ProductQtyReportRepository productQtyReportRepository;
	private BeanItemContainer<RfqHeader> _activeRfqsContainer; // lazy
	
	@Autowired
	@Lazy
	private IRfQService rfqService;
	
	private ISendService sendService = new SendService();


	private MFSession(final Builder builder)
	{
		super();
		Application.autowire(this);
		
		this.user = builder.getUser();
		
		final BPartner bpartner = user.getBpartner();
		this.contracts = builder.getContractsRepository().getContracts(bpartner);
		
		this.productQtyReportRepository = new ProductQtyReportRepository(user, contracts);
	}

	/** @return current logged user; never returns null */
	public User getUser()
	{
		return user;
	}

	public Contracts getContracts()
	{
		return contracts;
	}
	
	public ProductQtyReportRepository getProductQtyReportRepository()
	{
		return productQtyReportRepository;
	}
	
	public BeanItemContainer<RfqHeader> getActiveRfqs()
	{
		if (_activeRfqsContainer == null)
		{
			final List<RfqHeader> activeRfqs = rfqService.getActiveRfqHeaders(user);
			_activeRfqsContainer = new BeanItemContainer(RfqHeader.class, activeRfqs);
		}
		return _activeRfqsContainer;
	}
	
	public ISendService getSendService()
	{
		return sendService;
	}
	
	public static final class Builder
	{
		private User user;
		private IContractsService contractsRepository;

		private Builder()
		{
			super();
		}

		public MFSession build()
		{
			return new MFSession(this);
		}

		public Builder setUser(User user)
		{
			this.user = user;
			return this;
		}

		private User getUser()
		{
			Preconditions.checkNotNull(user, "user is null");
			return user;
		}

		public Builder setContractsRepository(IContractsService contractsRepository)
		{
			this.contractsRepository = contractsRepository;
			return this;
		}

		public IContractsService getContractsRepository()
		{
			Preconditions.checkNotNull(contractsRepository, "contractsRepository is null");
			return contractsRepository;
		}

	}
}
