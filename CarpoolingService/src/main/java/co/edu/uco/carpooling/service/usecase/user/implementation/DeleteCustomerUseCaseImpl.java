package co.edu.uco.carpooling.service.usecase.user.implementation;


import co.edu.uco.carpooling.dto.CustomerDTO;
import co.edu.uco.carpooling.entity.CustomerEntity;
import co.edu.uco.carpooling.service.domain.CustomerDomain;
import co.edu.uco.carpooling.service.mapper.entityassembler.EntityAssembler;
import co.edu.uco.carpooling.service.port.repository.CustomerRepository;
import co.edu.uco.carpooling.service.usecase.user.DeleteCustomerUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteCustomerUseCaseImpl implements DeleteCustomerUseCase {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private EntityAssembler<CustomerEntity, CustomerDomain, CustomerDTO> assemblerService;
    @Override
    public void execute(CustomerDomain domain) {
        CustomerEntity customerEntity = assemblerService.assembleEntity(domain);
        customerRepository.deleteById(customerEntity.getId());
    }
}
