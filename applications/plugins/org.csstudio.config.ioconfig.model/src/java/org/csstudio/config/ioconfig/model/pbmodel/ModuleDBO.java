/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
/*
 * $Id: Module.java,v 1.5 2010/08/20 13:33:08 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model.pbmodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.DocumentDBO;
import org.csstudio.config.ioconfig.model.INodeVisitor;
import org.csstudio.config.ioconfig.model.INodeWithPrototype;
import org.csstudio.config.ioconfig.model.NodeType;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdFileParser;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdModuleModel2;
import org.hibernate.annotations.BatchSize;

/**
 * @author gerke
 * @author $Author: hrickens $
 * @version $Revision: 1.5 $
 * @since 22.03.2007
 */

@Entity
@BatchSize(size = 32)
@Table(name = "ddb_Profibus_Module")
public class ModuleDBO extends AbstractNodeDBO<SlaveDBO, ChannelStructureDBO> implements
        INodeWithPrototype {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * The number of module at the GSD File.
     */
    private int _moduleNumber = -1;
    
    private List<Integer> _configurationData = new ArrayList<Integer>();
    
    private int _inputSize;
    
    private int _outputSize;
    
    private int _inputOffset;
    
    private int _outputOffset;
    
    @Transient
    private String _extModulePrmDataLen;
    
    /**
     * This Constructor is only used by Hibernate. To create an new {@link ModuleDBO}
     * {@link #Module(SlaveDBO)}
     */
    public ModuleDBO() {
        // Constructor for Hiberrnate
    }
    
    /**
     * The default Constructor.
     * @param slave the parent Slave.
     * @throws PersistenceException 
     */
    public ModuleDBO(@Nonnull final SlaveDBO slave) throws PersistenceException {
        setParent(slave);
        slave.addChild(this);
    }
    
    /*
     * Die length ergibt sich daraus das die ConfigurationData maxmal 20 byte enthalten darf. Da
     * jedes Byte als Hex String (z.B. 0x01) gespeichert wird muss dieser Wert mal 4 genommen
     * werden. Weiter sind die Werte per Komma getrennt was bis zu weitern 19 Stellen erfordern
     * kann. length = 204+19
     */
    @Column(name = "cfg_data", length = 99)
    @Nonnull
    public String getConfigurationData() {
        return GsdFileParser.intList2HexString(_configurationData);
    }
    
    /**
     * @return
     */
    @Transient
    @Nonnull
    public List<Integer> getConfigurationDataList() {
        return _configurationData;
    }
    
    public void setConfigurationData(@CheckForNull final String configurationData) {
        if(configurationData != null && !configurationData.trim().isEmpty()) {
            final String[] split = configurationData.split(",");
            _configurationData = new ArrayList<Integer>();
            for (String value : split) {
                _configurationData.add(GsdFileParser.gsdValue2Int(value));
            }
        }
    }
    
    @Transient
    public void setConfigurationDataByte(@Nonnull final Integer index, @Nonnull final Integer value) {
        _configurationData.set(index, value);
    }
    
    @Transient
    public void setConfigurationData(@Nonnull final List<Integer> configurationDataList) {
        _configurationData = configurationDataList;
    }
    
    /**
     *
     * @return the input offset
     * @throws PersistenceException 
     */
    @Transient
    public int getInputOffsetNH() throws PersistenceException {
        if(getSlave() != null) {
            ModuleDBO module = null;
            int sub = 1;
            while ( (module == null) && ( (getSortIndex() - sub) >= 0)) {
                module = (ModuleDBO) getSlave().getChildrenAsMap()
                        .get((short) (getSortIndex() - sub));
                sub++;
            }
            if(module != null) {
                final int inputOffset = module.getInputOffsetNH() + module.getInputSize();
                return inputOffset;
            }
        }
        return 0;
    }
    
    /**
     *
     * @return the input offset
     */
    public int getInputOffset() {
        return _inputOffset;
    }
    
    /**
     *
     * @param inputOffset set the input offset.
     */
    public void setInputOffset(final int inputOffset) {
        this._inputOffset = inputOffset;
    }
    
    @Transient
    public int getOutputOffsetNH() throws PersistenceException {
        if(getSlave() != null) {
            ModuleDBO module = null;
            int sub = 1;
            while ( (module == null) && ( (getSortIndex() - sub) >= 0)) {
                module = (ModuleDBO) getSlave().getChildrenAsMap()
                        .get((short) (getSortIndex() - sub));
                sub++;
            }
            if(module != null) {
                final int outputOffset = module.getOutputOffsetNH() + module.getOutputSize();
                return outputOffset;
            }
        }
        return 0;
    }
    
    public int getOutputOffset() {
        return _outputOffset;
    }
    
    public void setOutputOffset(final int outputOffset) {
        this._outputOffset = outputOffset;
    }
    
    public int getInputSize() {
        return _inputSize;
    }
    
    public void setInputSize(final int inputSize) {
        this._inputSize = inputSize;
    }
    
    public int getOutputSize() {
        return _outputSize;
    }
    
    public void setOutputSize(final int outputSize) {
        this._outputSize = outputSize;
    }
    
    public int getModuleNumber() {
        return _moduleNumber;
    }
    
    public void setModuleNumber(final int moduleNumber) {
        _moduleNumber = moduleNumber;
    }
    
    @Transient
    @CheckForNull
    public GSDModuleDBO getGSDModule() {
        GSDModuleDBO gsdModule = null;
        final GSDFileDBO gsdFile = getGSDFile();
        if(gsdFile != null) {
            gsdModule = gsdFile.getGSDModule(getModuleNumber());
        }
        return gsdModule;
    }
    
    @ManyToOne
    @Nonnull
    public SlaveDBO getSlave() {
        return (SlaveDBO) getParent();
    }
    
    public void setSlave(@Nonnull final SlaveDBO slave) {
        this.setParent(slave);
    }
    
    /**
     *
     * @return the Slave GSD File.
     */
    @Transient
    @CheckForNull
    public GSDFileDBO getGSDFile() {
        return getSlave().getGSDFile();
    }
    
    @Transient
    @CheckForNull
    public String getExtModulePrmDataLen() {
        return _extModulePrmDataLen;
    }
    
    /**
     * @param trim
     */
    public void setExtModulePrmDataLen(@Nonnull final String extModulePrmDataLen) {
        _extModulePrmDataLen = extModulePrmDataLen;
    }
    
    @Transient
    @Nonnull
    public String getEpicsAddressString() {
        /** contribution to ioName (PV-link to EPICSORA) */
        return getSlave().getEpicsAdressString();
    }
    
    @Transient
    @CheckForNull
    public GsdModuleModel2 getGsdModuleModel2() {
        GsdModuleModel2 module = null;
        final GSDFileDBO gsdFile = getParent().getGSDFile();
        if(gsdFile != null) {
            module = gsdFile.getParsedGsdFileModel().getModule(getModuleNumber());
        }
        return module;
    }
    
    @Transient
    public short getMaxOffset() throws IOException {
        final GsdModuleModel2 gsdModuleModel2 = getGsdModuleModel2();
        if(gsdModuleModel2 != null) {
            short offset = getSortIndex();
            final List<Integer> values = gsdModuleModel2.getValue();
            for (Integer value : values) {
                final SlaveCfgData slaveCfgData = new SlaveCfgData(value);
                int byteMulti = 1;
                if(slaveCfgData.isWordSize()) {
                    byteMulti = 2;
                }
                offset += (slaveCfgData.getNumber() * byteMulti);
            }
            return offset;
        }
        return -1;
    }
    
    @Override
    @Nonnull
    public ModuleDBO copyThisTo(@Nonnull final SlaveDBO parentNode) throws PersistenceException {
        final ModuleDBO copy = (ModuleDBO) super.copyThisTo(parentNode);
        copy.setName(getName());
        return copy;
    }
    
    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    @Nonnull
    public ModuleDBO copyParameter(@Nonnull final SlaveDBO parentNode) throws PersistenceException {
        final SlaveDBO slave = parentNode;
        final ModuleDBO copy = new ModuleDBO(slave);
        copy.setModuleNumber(getModuleNumber());
        if(slave.getChildrenAsMap().get(getSortIndex()) == null) {
            copy.setSortIndex((int) getSortIndex());
        }
        //            copy.setDocuments(getDocuments());
        copy.setConfigurationData(getConfigurationData());
        String extModulePrmDataLen = getExtModulePrmDataLen();
        extModulePrmDataLen = extModulePrmDataLen == null?"":extModulePrmDataLen;
        copy.setExtModulePrmDataLen(extModulePrmDataLen);
        
        for (ChannelStructureDBO node : getChildrenAsMap().values()) {
            final ChannelStructureDBO childrenCopy = node.copyThisTo(copy);
            childrenCopy.setSortIndexNonHibernate(node.getSortIndex());
        }
        
        return copy;
    }
    
    @Override
    public void localUpdate() {
        // make Offset
        int input;
        int output;
        
        // make Size
        input = 0;
        output = 0;
        
        final Set<ChannelStructureDBO> channelStructs = getChildren();
        for (ChannelStructureDBO channelStructure : channelStructs) {
            final Set<ChannelDBO> channels = channelStructure.getChildren();
            for (ChannelDBO channel : channels) {
                int bitSize;
                try {
                    bitSize = channel.getChannelType().getBitSize();
                } catch (final IllegalArgumentException e) {
                    bitSize = 0;
                }
                if(channel.isInput()) {
                    input += bitSize;
                } else {
                    output += bitSize;
                }
            }
        }
        if(input / 8 != getInputSize()) {
            setInputSize(input / 8);
        }
        if(output / 8 != getOutputSize()) {
            setOutputSize(output / 8);
        }
    }
    
    @Override
    public void update() throws PersistenceException {
        super.update();
    }
    
    @Transient
    @Nonnull
    public Set<ChannelDBO> getPureChannels() {
        final Set<ChannelDBO> result = new HashSet<ChannelDBO>();
        for (ChannelStructureDBO s : getChildren()) {
            if(s.isSimple()) {
                result.addAll(s.getChildren());
            }
        }
        return result;
    }
    
    @Transient
    @Nonnull
    public String getExtUserPrmDataConst() {
        if(getConfigurationData() == null) {
            List<Integer> extUserPrmDataConst;
            String defaultUserPrmDataConst;
            extUserPrmDataConst = getGsdModuleModel2().getExtUserPrmDataConst();
            defaultUserPrmDataConst = GsdFileParser.intList2HexString(extUserPrmDataConst);
            return defaultUserPrmDataConst;
        }
        return getConfigurationData();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    @Nonnull
    public NodeType getNodeType() {
        return NodeType.MODULE;
    }
    
    /**
     * @return The Name of this Node.
     */
    @Override
    @Nonnull
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if(getSortIndex() != null) {
            sb.append(getSortIndex());
        }
        sb.append('[');
        sb.append(getModuleNumber());
        sb.append(']');
        if(getName() != null) {
            sb.append(':');
            sb.append(getName());
        }
        return sb.toString();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    @Transient
    public Set<DocumentDBO> getPrototypeDocuments() {
        final GSDModuleDBO gsdModule = getGSDModule();
        return gsdModule == null ? new HashSet<DocumentDBO>() : gsdModule.getDocuments();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ChannelStructureDBO createChild() throws PersistenceException {
        throw new UnsupportedOperationException("No simple child can be created for node type "
                + getClass().getName());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(@Nonnull final INodeVisitor visitor) {
        visitor.visit(this);
    }
    
    /**
     * @param newModuleNumber
     */
    public final void setNewModel(final int newModuleNumber, @Nonnull final String createdBy) throws PersistenceException {
        removeAllChild();
        setModuleNumber(newModuleNumber);
        final GSDModuleDBO gsdModule = getGSDModule();
        
        // Unknown Module (--> Config the Epics Part)
        if(gsdModule == null) {
            throw new IllegalArgumentException("Module has no GSD Module");
        }
        createChannels(newModuleNumber, this, gsdModule, createdBy);
    }
    
    /**
     * @param selectedModuleNo
     * @param hasChanged
     * @param module
     * @param gsdModule
     * @param topGroup 
     * @throws PersistenceException 
     */
    private void createChannels(final int selectedModuleNo,
                                @Nonnull final ModuleDBO module,
                                @Nonnull final GSDModuleDBO gsdModule, @Nonnull final String createdBy) throws PersistenceException {
        // TODO (hrickens) [05.05.2011]:Kann die Abfrage nicht vereinfacht werden.
        final GSDFileDBO gsdFile = gsdModule.getGSDFile();
        if(gsdFile!=null) {
            final GsdModuleModel2 module2 = gsdFile.getParsedGsdFileModel().getModule(selectedModuleNo);
            if(module2!=null) {
                module.setConfigurationData(module2.getExtUserPrmDataConst());
            }
        }
        // Generate Input Channel
        final TreeSet<ModuleChannelPrototypeDBO> moduleChannelPrototypes = gsdModule
                .getModuleChannelPrototypeNH();
        if(moduleChannelPrototypes != null) {
            final ModuleChannelPrototypeDBO[] array = moduleChannelPrototypes
                    .toArray(new ModuleChannelPrototypeDBO[0]);
            for (int sortIndex = 0; sortIndex < array.length; sortIndex++) {
                final ModuleChannelPrototypeDBO prototype = array[sortIndex];
                makeNewChannel(prototype, sortIndex, createdBy);
            }
        }
        module.localUpdate();
        module.localSave();
    }
    
    private void makeNewChannel(@Nonnull final ModuleChannelPrototypeDBO channelPrototype,
                                final int sortIndex, @Nonnull final String createdBy) throws PersistenceException {
        if(channelPrototype.isStructure()) {
            makeStructChannel(channelPrototype, sortIndex, createdBy);
        } else {
            makeNewPureChannel(channelPrototype, sortIndex, createdBy);
        }
    }
    
    private void makeStructChannel(@Nonnull final ModuleChannelPrototypeDBO channelPrototype, final int sortIndex, @Nonnull final String createdBy) throws PersistenceException {
        channelPrototype.getOffset();
        final Date now = new Date();
        final ChannelStructureDBO channelStructure = ChannelStructureDBO.makeChannelStructure(this,channelPrototype);
        channelStructure.setCreatedOn(now);
        channelStructure.setUpdatedOn(now);
        channelStructure.setCreatedBy(createdBy);
        channelStructure.setUpdatedBy(createdBy);
        channelStructure.moveSortIndex(sortIndex);
        channelPrototype.save();
    }
    
    private void makeNewPureChannel(@Nonnull final ModuleChannelPrototypeDBO channelPrototype, final int sortIndex, @Nonnull final String createdBy) throws PersistenceException {
        final Date now = new Date();
        final boolean isDigi = channelPrototype.getType().getBitSize() == 1;
        final ChannelStructureDBO cs = ChannelStructureDBO.makeSimpleChannel(this,
                                                                       channelPrototype.getName(),
                                                                       channelPrototype.isInput(),
                                                                       isDigi);
        cs.moveSortIndex(sortIndex);
        final ChannelDBO channel = cs.getFirstChannel();
        if (channel != null) {
            channel.setCreatedOn(now);
            channel.setUpdatedOn(now);
            channel.setCreatedBy(createdBy);
            channel.setUpdatedBy(createdBy);
            channel.setChannelTypeNonHibernate(channelPrototype.getType());
            channel.setStatusAddressOffset(channelPrototype.getShift());
            channel.moveSortIndex(sortIndex);
            channel.setChannelNumber(channelPrototype.getOffset());
        }
    }
    
    @Override
    public boolean equals(@CheckForNull final Object obj) {
        return super.equals(obj);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
