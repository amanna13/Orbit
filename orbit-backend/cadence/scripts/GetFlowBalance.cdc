import FungibleToken from 0xee82856bf20e2aa6
import FlowToken from 0x0ae53cb6e3f42a79

// Script to get the Flow token balance of an account
// Arguments:
//   - address: The address to check
access(all) fun main(address: Address): UFix64 {
    let account = getAccount(address)
    
    let vaultRef = account.capabilities
        .get<&FlowToken.Vault>(/public/flowTokenBalance)
        .borrow()
        ?? panic("Could not borrow FlowToken Vault reference")
    
    return vaultRef.balance
}
