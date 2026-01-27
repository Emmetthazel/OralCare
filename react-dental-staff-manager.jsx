import React, { useState, useEffect } from 'react';
import { Search, Plus, Edit2, Trash2, Key, X, Save, UserPlus, Building2, ChevronDown, ChevronRight, Users, Mail, Phone, Calendar, MapPin, Lock, User } from 'lucide-react';

// Service API pour communiquer avec le backend Java
const userService = {
  async loadHierarchy(searchQuery = '') {
    try {
      const response = await fetch(`/api/users/hierarchy?search=${searchQuery}`);
      return await response.json();
    } catch (error) {
      console.error('Erreur:', error);
      return {};
    }
  },

  async getUserDetails(email) {
    const response = await fetch(`/api/users/${email}`);
    return await response.json();
  },

  async updateUser(email, updates) {
    const response = await fetch(`/api/users/${email}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(updates)
    });
    return response.json();
  },

  async deleteUser(email) {
    await fetch(`/api/users/${email}`, { method: 'DELETE' });
  },

  async resetPassword(email) {
    const response = await fetch(`/api/users/${email}/reset-password`, {
      method: 'POST'
    });
    return await response.json();
  },

  async addNewUser(cabinetName, roleType, userData) {
    const response = await fetch('/api/users', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        cabinet: cabinetName,
        role: roleType,
        ...userData
      })
    });
    return response.json();
  }
};

const DentalStaffManager = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [staffData, setStaffData] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedUser, setSelectedUser] = useState(null);
  const [expandedCabinets, setExpandedCabinets] = useState({});
  const [editingUser, setEditingUser] = useState(null);
  const [showCreateDialog, setShowCreateDialog] = useState(false);
  const [createContext, setCreateContext] = useState({ cabinet: '', role: '' });

  // Charger les données au montage
  useEffect(() => {
    loadData();
  }, []);

  // Recharger quand la recherche change (avec debounce)
  useEffect(() => {
    const timer = setTimeout(() => {
      loadData(searchQuery);
    }, 300);
    return () => clearTimeout(timer);
  }, [searchQuery]);

  const loadData = async (search = '') => {
    try {
      setLoading(true);
      const data = await userService.loadHierarchy(search);
      setStaffData(data);
      setError(null);
    } catch (err) {
      setError('Erreur de chargement des données');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteUser = async (email) => {
    if (!window.confirm('Êtes-vous sûr ?')) return;
    
    try {
      await userService.deleteUser(email);
      await loadData(searchQuery);
      alert('Utilisateur supprimé avec succès');
    } catch (err) {
      alert('Erreur lors de la suppression');
    }
  };

  const handleSaveUser = async (email, updates) => {
    try {
      await userService.updateUser(email, updates);
      await loadData(searchQuery);
      alert('Modifications enregistrées');
    } catch (err) {
      alert('Erreur lors de la sauvegarde');
    }
  };

  const handleResetPassword = async (email) => {
    try {
      const newPassword = await userService.resetPassword(email);
      alert(`Nouveau mot de passe : ${newPassword}`);
      await loadData(searchQuery);
    } catch (err) {
      alert('Erreur lors de la réinitialisation');
    }
  };

  const toggleCabinet = (cabinetName) => {
    setExpandedCabinets(prev => ({
      ...prev,
      [cabinetName]: !prev[cabinetName]
    }));
  };

  if (loading) return <div>Chargement...</div>;
  if (error) return <div>Erreur : {error}</div>;

  return (
    <div style={{
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%)',
      fontFamily: '"Crimson Pro", Georgia, serif'
    }}>
      {/* Header */}
      <header style={{
        background: 'linear-gradient(135deg, #1a365d 0%, #2c5282 100%)',
        color: 'white',
        padding: '2.5rem 3rem',
        boxShadow: '0 8px 32px rgba(0,0,0,0.12)'
      }}>
        <div style={{ maxWidth: '1400px', margin: '0 auto' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
            <Building2 size={42} />
            <div>
              <h1 style={{
                fontSize: '2.5rem',
                fontWeight: '300',
                margin: 0,
                fontFamily: '"Playfair Display", serif'
              }}>
                Gestion du Personnel
              </h1>
              <p style={{ fontSize: '1rem', margin: '0.5rem 0 0 0', opacity: 0.9 }}>
                Administration des Cabinets Dentaires
              </p>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <div style={{ maxWidth: '1400px', margin: '0 auto', padding: '2.5rem 3rem' }}>
        {/* Search & Actions Bar */}
        <div style={{
          background: 'white',
          padding: '1.75rem 2rem',
          borderRadius: '16px',
          marginBottom: '2rem',
          boxShadow: '0 4px 20px rgba(0,0,0,0.08)',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          gap: '2rem'
        }}>
          <div style={{ position: 'relative', flex: 1, maxWidth: '400px' }}>
            <Search
              size={20}
              style={{
                position: 'absolute',
                left: '1rem',
                top: '50%',
                transform: 'translateY(-50%)',
                color: '#718096'
              }}
            />
            <input
              type="text"
              placeholder="Rechercher par nom ou email..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              style={{
                width: '100%',
                padding: '0.875rem 1rem 0.875rem 3rem',
                border: '2px solid #e2e8f0',
                borderRadius: '12px',
                fontSize: '0.95rem'
              }}
            />
          </div>

          <button
            onClick={() => {
              setShowCreateDialog(true);
              setCreateContext({ cabinet: '', role: '' });
            }}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.75rem',
              padding: '0.875rem 1.75rem',
              background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              color: 'white',
              border: 'none',
              borderRadius: '12px',
              cursor: 'pointer'
            }}
          >
            <Plus size={20} />
            Nouveau Cabinet
          </button>
        </div>

        {/* Cabinets List */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          {Object.entries(staffData).map(([cabinetName, staff]) => (
            <div key={cabinetName} style={{
              background: 'white',
              borderRadius: '16px',
              overflow: 'hidden',
              boxShadow: '0 4px 20px rgba(0,0,0,0.08)'
            }}>
              {/* Cabinet Header */}
              <div
                onClick={() => toggleCabinet(cabinetName)}
                style={{
                  background: 'linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%)',
                  padding: '1.5rem 2rem',
                  cursor: 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '1rem',
                  borderBottom: '2px solid #e2e8f0'
                }}
              >
                {expandedCabinets[cabinetName] ? <ChevronDown size={24} /> : <ChevronRight size={24} />}
                <Building2 size={24} color="#4c51bf" />
                <h2 style={{
                  fontSize: '1.4rem',
                  fontWeight: '400',
                  margin: 0,
                  flex: 1,
                  color: '#2d3748',
                  fontFamily: '"Playfair Display", serif'
                }}>
                  {cabinetName}
                </h2>
                <span style={{
                  background: '#667eea',
                  color: 'white',
                  padding: '0.4rem 1rem',
                  borderRadius: '20px',
                  fontSize: '0.85rem'
                }}>
                  {staff.length} membre{staff.length > 1 ? 's' : ''}
                </span>
              </div>

              {/* Staff Lists */}
              {expandedCabinets[cabinetName] && (
                <div style={{ padding: '1.5rem 2rem' }}>
                  {/* Médecins Section */}
                  <div style={{ marginBottom: '2rem' }}>
                    <div style={{
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between',
                      marginBottom: '1rem',
                      paddingBottom: '0.75rem',
                      borderBottom: '2px solid #e2e8f0'
                    }}>
                      <h3 style={{
                        fontSize: '1.1rem',
                        fontWeight: '500',
                        color: '#4a5568',
                        margin: 0,
                        fontStyle: 'italic'
                      }}>
                        MÉDECINS
                      </h3>
                      <button
                        onClick={() => {
                          setShowCreateDialog(true);
                          setCreateContext({ cabinet: cabinetName, role: 'MÉDECIN' });
                        }}
                        style={{
                          display: 'flex',
                          alignItems: 'center',
                          gap: '0.5rem',
                          padding: '0.5rem 1rem',
                          background: '#48bb78',
                          color: 'white',
                          border: 'none',
                          borderRadius: '8px',
                          cursor: 'pointer'
                        }}
                      >
                        <UserPlus size={16} />
                        Ajouter
                      </button>
                    </div>
                    {staff.filter(u => u.role === 'MÉDECIN').map(user => (
                      <UserRow
                        key={user.email}
                        user={user}
                        isSelected={selectedUser === user.email}
                        isEditing={editingUser === user.email}
                        onSelect={() => setSelectedUser(user.email)}
                        onEdit={() => setEditingUser(user.email)}
                        onDelete={() => handleDeleteUser(user.email)}
                        onSave={(updates) => handleSaveUser(user.email, updates)}
                        onCancel={() => setEditingUser(null)}
                        onResetPassword={() => handleResetPassword(user.email)}
                      />
                    ))}
                  </div>

                  {/* Secrétaires Section */}
                  <div>
                    <div style={{
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between',
                      marginBottom: '1rem',
                      paddingBottom: '0.75rem',
                      borderBottom: '2px solid #e2e8f0'
                    }}>
                      <h3 style={{
                        fontSize: '1.1rem',
                        fontWeight: '500',
                        color: '#4a5568',
                        margin: 0,
                        fontStyle: 'italic'
                      }}>
                        SECRÉTAIRES
                      </h3>
                      <button
                        onClick={() => {
                          setShowCreateDialog(true);
                          setCreateContext({ cabinet: cabinetName, role: 'SECRÉTAIRE' });
                        }}
                        style={{
                          display: 'flex',
                          alignItems: 'center',
                          gap: '0.5rem',
                          padding: '0.5rem 1rem',
                          background: '#48bb78',
                          color: 'white',
                          border: 'none',
                          borderRadius: '8px',
                          cursor: 'pointer'
                        }}
                      >
                        <UserPlus size={16} />
                        Ajouter
                      </button>
                    </div>
                    {staff.filter(u => u.role === 'SECRÉTAIRE').map(user => (
                      <UserRow
                        key={user.email}
                        user={user}
                        isSelected={selectedUser === user.email}
                        isEditing={editingUser === user.email}
                        onSelect={() => setSelectedUser(user.email)}
                        onEdit={() => setEditingUser(user.email)}
                        onDelete={() => handleDeleteUser(user.email)}
                        onSave={(updates) => handleSaveUser(user.email, updates)}
                        onCancel={() => setEditingUser(null)}
                        onResetPassword={() => handleResetPassword(user.email)}
                      />
                    ))}
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      </div>

      {/* Create User Dialog */}
      {showCreateDialog && (
        <CreateUserDialog
          context={createContext}
          onClose={() => setShowCreateDialog(false)}
          onSubmit={async (userData) => {
            try {
              await userService.addNewUser(createContext.cabinet, createContext.role, userData);
              await loadData(searchQuery);
              setShowCreateDialog(false);
              alert('Utilisateur créé avec succès');
            } catch (err) {
              alert('Erreur lors de la création');
            }
          }}
        />
      )}
    </div>
  );
};

// User Row Component
const UserRow = ({ user, isSelected, isEditing, onSelect, onEdit, onDelete, onSave, onCancel, onResetPassword }) => {
  const [formData, setFormData] = useState(user);

  useEffect(() => {
    setFormData(user);
  }, [user]);

  if (isEditing) {
    return (
      <div style={{
        background: 'linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%)',
        border: '2px solid #667eea',
        borderRadius: '12px',
        padding: '1.5rem',
        marginBottom: '1rem'
      }}>
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(2, 1fr)',
          gap: '1.25rem',
          marginBottom: '1.5rem'
        }}>
          <InputField icon={<User size={18} />} label="Login" value={formData.email.split('@')[0]} disabled />
          <InputField icon={<Lock size={18} />} label="Mot de passe" value={formData.password} disabled />
          <InputField icon={<User size={18} />} label="Nom" value={formData.nomComplet.split(' ').slice(1).join(' ')} onChange={(v) => setFormData({ ...formData, nomComplet: formData.nomComplet.split(' ')[0] + ' ' + v })} />
          <InputField icon={<User size={18} />} label="Prénom" value={formData.nomComplet.split(' ')[0]} onChange={(v) => setFormData({ ...formData, nomComplet: v + ' ' + formData.nomComplet.split(' ').slice(1).join(' ') })} />
          <InputField icon={<Mail size={18} />} label="Email" value={formData.email} disabled />
          <InputField icon={<Phone size={18} />} label="Téléphone" value={formData.tel} onChange={(v) => setFormData({ ...formData, tel: v })} />
          <InputField icon={<User size={18} />} label="CIN" value={formData.cin} onChange={(v) => setFormData({ ...formData, cin: v })} />
          <InputField icon={<Calendar size={18} />} label="Date de naissance" value={formData.dateNaissance} disabled />
          <InputField icon={<User size={18} />} label="Sexe" value={formData.sexe} disabled />
          <InputField icon={<MapPin size={18} />} label="Adresse" value={formData.adresse} disabled />
        </div>

        <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
          <button onClick={() => onSave(formData)} style={{
            display: 'flex', alignItems: 'center', gap: '0.5rem',
            padding: '0.75rem 1.5rem', background: '#48bb78', color: 'white',
            border: 'none', borderRadius: '8px', cursor: 'pointer'
          }}>
            <Save size={18} /> Sauvegarder
          </button>
          <button onClick={onResetPassword} style={{
            display: 'flex', alignItems: 'center', gap: '0.5rem',
            padding: '0.75rem 1.5rem', background: '#ed8936', color: 'white',
            border: 'none', borderRadius: '8px', cursor: 'pointer'
          }}>
            <Key size={18} /> Réinitialiser MDP
          </button>
          <button onClick={onCancel} style={{
            display: 'flex', alignItems: 'center', gap: '0.5rem',
            padding: '0.75rem 1.5rem', background: '#e53e3e', color: 'white',
            border: 'none', borderRadius: '8px', cursor: 'pointer'
          }}>
            <X size={18} /> Annuler
          </button>
        </div>
      </div>
    );
  }

  return (
    <div
      onClick={onSelect}
      onDoubleClick={onEdit}
      style={{
        background: isSelected ? 'linear-gradient(135deg, #e8eaf6 0%, #f3e5f5 100%)' : 'white',
        border: `1px solid ${isSelected ? '#667eea' : '#e2e8f0'}`,
        borderRadius: '10px',
        padding: '1rem 1.5rem',
        marginBottom: '0.75rem',
        cursor: 'pointer',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between'
      }}
    >
      <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem', flex: 1 }}>
        <div style={{
          width: '48px', height: '48px', borderRadius: '12px',
          background: user.role === 'MÉDECIN' 
            ? 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
            : 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          color: 'white', fontSize: '1.2rem', fontWeight: '500'
        }}>
          {user.nomComplet.split(' ').map(n => n[0]).join('').slice(0, 2)}
        </div>
        <div style={{ flex: 1 }}>
          <div style={{ fontSize: '1.05rem', fontWeight: '500', color: '#2d3748', marginBottom: '0.25rem' }}>
            {user.nomComplet}
          </div>
          <div style={{ fontSize: '0.9rem', color: '#718096', fontFamily: 'monospace' }}>
            {user.email}
          </div>
        </div>
      </div>

      <div style={{ display: 'flex', gap: '0.5rem' }}>
        <button onClick={(e) => { e.stopPropagation(); onEdit(); }} style={{
          padding: '0.5rem', background: 'transparent', border: '1px solid #cbd5e0',
          borderRadius: '8px', cursor: 'pointer', color: '#4a5568'
        }}>
          <Edit2 size={16} />
        </button>
        <button onClick={(e) => { e.stopPropagation(); onDelete(); }} style={{
          padding: '0.5rem', background: 'transparent', border: '1px solid #fc8181',
          borderRadius: '8px', cursor: 'pointer', color: '#e53e3e'
        }}>
          <Trash2 size={16} />
        </button>
      </div>
    </div>
  );
};

// Input Field Component
const InputField = ({ icon, label, value, onChange, disabled }) => (
  <div>
    <label style={{ display: 'block', fontSize: '0.85rem', color: '#4a5568', marginBottom: '0.5rem' }}>
      {label}
    </label>
    <div style={{ position: 'relative' }}>
      <div style={{ position: 'absolute', left: '0.75rem', top: '50%', transform: 'translateY(-50%)', color: disabled ? '#a0aec0' : '#718096' }}>
        {icon}
      </div>
      <input
        type="text"
        value={value}
        onChange={(e) => onChange && onChange(e.target.value)}
        disabled={disabled}
        style={{
          width: '100%',
          padding: '0.75rem 0.75rem 0.75rem 2.5rem',
          border: '2px solid #e2e8f0',
          borderRadius: '8px',
          fontSize: '0.9rem',
          background: disabled ? '#f7fafc' : 'white',
          color: disabled ? '#a0aec0' : '#2d3748'
        }}
      />
    </div>
  </div>
);

// Create User Dialog Component
const CreateUserDialog = ({ context, onClose, onSubmit }) => {
  const [formData, setFormData] = useState({
    nom: '', prenom: '', email: '', cin: '', tel: '',
    sexe: 'MALE', dateNaissance: '', pays: 'Maroc',
    ville: '', specialite: '', numCnss: ''
  });

  const generatedLogin = formData.prenom && formData.nom 
    ? `${formData.prenom[0].toLowerCase()}.${formData.nom.toLowerCase()}` 
    : 'p.nom (auto)';

  const generatedPassword = 'temp' + Math.random().toString(36).slice(-8);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!formData.nom || !formData.prenom || !formData.email) {
      alert('Nom, Prénom et Email sont obligatoires.');
      return;
    }
    onSubmit({ ...formData, login: generatedLogin, password: generatedPassword });
  };

  return (
    <div style={{
      position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
      background: 'rgba(0,0,0,0.6)', display: 'flex',
      alignItems: 'center', justifyContent: 'center', zIndex: 1000
    }}>
      <div style={{
        background: 'white', borderRadius: '20px', maxWidth: '800px',
        width: '100%', maxHeight: '90vh', overflow: 'auto',
        boxShadow: '0 25px 50px rgba(0,0,0,0.25)'
      }}>
        <div style={{
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          color: 'white', padding: '2rem',
          borderRadius: '20px 20px 0 0',
          display: 'flex', alignItems: 'center', justifyContent: 'space-between'
        }}>
          <div>
            <h2 style={{ fontSize: '1.8rem', fontWeight: '400', margin: '0 0 0.5rem 0' }}>
              Création : {context.role || 'Personnel'}
            </h2>
            <p style={{ margin: 0, opacity: 0.9, fontSize: '0.95rem' }}>
              {context.cabinet || 'Nouveau Cabinet'}
            </p>
          </div>
          <button onClick={onClose} style={{
            background: 'rgba(255,255,255,0.2)', border: 'none',
            color: 'white', padding: '0.5rem', borderRadius: '8px', cursor: 'pointer'
          }}>
            <X size={24} />
          </button>
        </div>

        <form onSubmit={handleSubmit} style={{ padding: '2rem' }}>
          <div style={{
            display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)',
            gap: '1.5rem', marginBottom: '2rem'
          }}>
            <InputField icon={<User size={18} />} label="Nom *" value={formData.nom} onChange={(v) => setFormData({ ...formData, nom: v })} />
            <InputField icon={<User size={18} />} label="Prénom *" value={formData.prenom} onChange={(v) => setFormData({ ...formData, prenom: v })} />
            <InputField icon={<User size={18} />} label="Login généré" value={generatedLogin} disabled />
            <InputField icon={<Mail size={18} />} label="Email (Contact) *" value={formData.email} onChange={(v) => setFormData({ ...formData, email: v })} />
            <InputField icon={<Lock size={18} />} label="Mot de passe" value={generatedPassword} disabled />
            <InputField icon={<User size={18} />} label="CIN" value={formData.cin} onChange={(v) => setFormData({ ...formData, cin: v })} />
            <InputField icon={<Phone size={18} />} label="Téléphone" value={formData.tel} onChange={(v) => setFormData({ ...formData, tel: v })} />
            <div>
              <label style={{ display: 'block', fontSize: '0.85rem', color: '#4a5568', marginBottom: '0.5rem' }}>Sexe</label>
              <select value={formData.sexe} onChange={(e) => setFormData({ ...formData, sexe: e.target.value })} style={{
                width: '100%', padding: '0.75rem', border: '2px solid #e2e8f0',
                borderRadius: '8px', fontSize: '0.9rem', background: 'white'
              }}>
                <option value="MALE">Homme</option>
                <option value="FEMALE">Femme</option>
              </select>
            </div>
            <InputField icon={<Calendar size={18} />} label="Date de naissance" value={formData.dateNaissance} onChange={(v) => setFormData({ ...formData, dateNaissance: v })} />
            <InputField icon={<MapPin size={18} />} label="Pays" value={formData.pays} onChange={(v) => setFormData({ ...formData, pays: v })} />
            <InputField icon={<MapPin size={18} />} label="Ville" value={formData.ville} onChange={(v) => setFormData({ ...formData, ville: v })} />
            {context.role === 'MÉDECIN' ? (
              <InputField icon={<User size={18} />} label="Spécialité" value={formData.specialite} onChange={(v) => setFormData({ ...formData, specialite: v })} />
            ) : (
              <InputField icon={<User size={18} />} label="N° CNSS" value={formData.numCnss} onChange={(v) => setFormData({ ...formData, numCnss: v })} />
            )}
          </div>

          <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
            <button type="button" onClick={onClose} style={{
              padding: '0.875rem 2rem', background: '#e2e8f0', color: '#4a5568',
              border: 'none', borderRadius: '10px', cursor: 'pointer'
            }}>
              Annuler
            </button>
            <button type="submit" style={{
              padding: '0.875rem 2rem', background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              color: 'white', border: 'none', borderRadius: '10px', cursor: 'pointer'
            }}>
              Créer le compte
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default DentalStaffManager;
