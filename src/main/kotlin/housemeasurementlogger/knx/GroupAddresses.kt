package housemeasurementlogger.knx

class GroupAddresses : Collection<GroupAddress> {
    private var addresses: Collection<GroupAddress>

    constructor() {
        addresses = emptyList()
    }

    constructor(addresses: Collection<GroupAddress>) {
        this.addresses = addresses
    }

    override val size: Int
        get() = addresses.size

    override fun contains(element: GroupAddress): Boolean {
        return addresses.contains(element)
    }

    override fun containsAll(elements: Collection<GroupAddress>): Boolean {
        return addresses.containsAll(elements)
    }

    override fun isEmpty(): Boolean {
        return addresses.isEmpty()
    }

    override fun iterator(): Iterator<GroupAddress> {
        return addresses.iterator()
    }
}
